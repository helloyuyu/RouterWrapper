package com.helloyuyu.routerwrapper.compiler.feature.transformers;

import com.squareup.javapoet.ClassName;
import com.helloyuyu.routerwrapper.compiler.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;


/**
 * @author xjs
 *         on  2018/1/15
 *         desc:
 */

public class ParcelableTypeTransformImpl extends BaseTypeTransform {


    public ParcelableTypeTransformImpl(Types typesUtils) {
        super(typesUtils);
    }

    @Override
    public boolean accept(Element element) {
        Element typeElement = typesUtils.asElement(element.asType());
        if (typeElement.asType().getKind() == TypeKind.DECLARED){
            DeclaredType declaredType = (DeclaredType) typeElement.asType();
            if (declaredType.getTypeArguments().size()>0){
                return false;
            }
        }
        return element instanceof TypeElement &&
                TypeUtils.recursionIsImplements(
                        (TypeElement) element
                        , ClassName.get("android.os", "Parcelable")
                        , typesUtils);
    }

    @Override
    public CharSequence transform(Element element) {
        return null;
    }
}
