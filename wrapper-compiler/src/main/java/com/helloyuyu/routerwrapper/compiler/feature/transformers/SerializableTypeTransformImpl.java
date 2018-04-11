package com.helloyuyu.routerwrapper.compiler.feature.transformers;

import com.squareup.javapoet.TypeName;
import com.helloyuyu.routerwrapper.compiler.feature.ITypeTransform;
import com.helloyuyu.routerwrapper.compiler.utils.TypeUtils;

import java.io.Serializable;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

/**
 * @author xjs
 *         on  2018/1/14
 *         desc:
 */

public class SerializableTypeTransformImpl implements ITypeTransform {

    private Types typeUtils;

    public SerializableTypeTransformImpl(Types typeUtils) {
        this.typeUtils = typeUtils;
    }

    @Override
    public boolean accept(Element element) {
        Element typeElement = typeUtils.asElement(element.asType());
        if (typeElement.asType().getKind() == TypeKind.DECLARED){
            DeclaredType declaredType = (DeclaredType) typeElement.asType();
            if (declaredType.getTypeArguments().size()>0){
                return false;
            }
        }
        return typeElement instanceof TypeElement
                && TypeUtils.recursionIsImplements(
                        (TypeElement) typeElement, TypeName.get(Serializable.class), typeUtils);
    }

    @Override
    public CharSequence transform(Element element) {
        return "Serializable";
    }

}
