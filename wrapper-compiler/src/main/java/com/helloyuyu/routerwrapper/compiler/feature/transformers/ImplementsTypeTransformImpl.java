package com.helloyuyu.routerwrapper.compiler.feature.transformers;


import com.helloyuyu.routerwrapper.compiler.feature.ITypeTransform;

import javax.lang.model.element.Element;

/**
 * @author xjs
 *         on  2018/1/15
 *         desc: 实现接口的获取
 */

public class ImplementsTypeTransformImpl implements ITypeTransform {

    @Override
    public boolean accept(Element element) {
        return false;
    }

    @Override
    public CharSequence transform(Element element) {
        return null;
    }
}
