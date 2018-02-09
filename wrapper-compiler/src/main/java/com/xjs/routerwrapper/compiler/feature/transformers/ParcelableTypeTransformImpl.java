package com.xjs.routerwrapper.compiler.feature.transformers;

import com.squareup.javapoet.ClassName;
import com.xjs.routerwrapper.compiler.utils.TypeUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
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
