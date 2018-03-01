package com.helloyuyu.routerwrapper.compiler.feature.transformers;

import com.squareup.javapoet.TypeName;
import com.helloyuyu.routerwrapper.compiler.feature.ITypeTransform;

import javax.lang.model.element.Element;

/**
 * @author xjs
 *         on  2018/1/14
 *         desc: String类型的
 */

public class StringTypeTransformImpl implements ITypeTransform {

    @Override
    public boolean accept(Element element) {
        return TypeName.get(element.asType()).toString().equals(String.class.getName());
    }

    @Override
    public CharSequence transform(Element element) {
        return "String";
    }
}
