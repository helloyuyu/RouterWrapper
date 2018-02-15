package com.helloyuyu.routerwrapper.compiler.feature.transformers;


import com.squareup.javapoet.TypeName;
import com.helloyuyu.routerwrapper.compiler.feature.ITypeTransform;

import javax.lang.model.element.Element;

/**
 * @author xjs
 *         on  2018/1/14
 *         desc: 基础类型的
 */

public class BoxPrimitiveTypeTransformImp implements ITypeTransform {


    @Override
    public boolean accept(Element element) {
        switch (TypeName.get(element.asType()).toString()) {
            //case "java.lang.Void":
            case "java.lang.Boolean":
            case "java.lang.Byte":
            case "java.lang.Short":
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Character":
            case "java.lang.Float":
            case "java.lang.Double":
                return true;
            default:
                return false;
        }
    }

    @Override
    public CharSequence transform(Element element) {
        switch (TypeName.get(element.asType()).toString()) {
            //case "java.lang.Void":
            case "java.lang.Boolean":
                return "Boolean";
            case "java.lang.Byte":
                return "Byte";
            case "java.lang.Short":
                return "Short";
            case "java.lang.Integer":
                return "Int";
            case "java.lang.Long":
                return "Long";
            case "java.lang.Character":
                return "Char";
            case "java.lang.Float":
                return "Float";
            case "java.lang.Double":
                return "Double";
            default:
                throw new RuntimeException("没有匹配该类型" + element.asType().toString());
        }
    }
}
