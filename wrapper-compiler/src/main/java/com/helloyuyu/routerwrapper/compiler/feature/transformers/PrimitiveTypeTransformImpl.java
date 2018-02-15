package com.helloyuyu.routerwrapper.compiler.feature.transformers;



import com.helloyuyu.routerwrapper.compiler.feature.ITypeTransform;

import javax.lang.model.element.Element;

/**
 * @author xjs
 *         on  2018/1/14
 *         desc: 基础类型的
 */

public class PrimitiveTypeTransformImpl implements ITypeTransform {


    @Override
    public boolean accept(Element element) {
        return element.asType().getKind().isPrimitive();
    }

    @Override
    public CharSequence transform(Element element) {
        switch (element.asType().getKind()) {
            case BOOLEAN:
                return "Boolean";
            case BYTE:
                return "Byte";
            case SHORT:
                return "Short";
            case INT:
                return "Int";
            case LONG:
                return "Long";
            case CHAR:
                return "Char";
            case FLOAT:
                return "Float";
            case DOUBLE:
                return "Double";
            default:
                break;
        }
        throw new RuntimeException("没有匹配该类型" + element.asType().toString());
    }
}
