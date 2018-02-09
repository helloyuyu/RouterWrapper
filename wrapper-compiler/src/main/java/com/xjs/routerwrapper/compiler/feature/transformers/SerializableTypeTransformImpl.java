package com.xjs.routerwrapper.compiler.feature.transformers;

import com.squareup.javapoet.TypeName;
import com.xjs.routerwrapper.compiler.feature.ITypeTransform;
import com.xjs.routerwrapper.compiler.utils.TypeUtils;

import java.io.Serializable;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
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
        return typeElement instanceof TypeElement
                && TypeUtils.recursionIsImplements(
                        (TypeElement) typeElement, TypeName.get(Serializable.class), typeUtils);
    }

    @Override
    public CharSequence transform(Element element) {
        return "Serializable";
    }

}
