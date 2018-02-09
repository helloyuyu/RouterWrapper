package com.xjs.routerwrapper.compiler.feature;

import javax.lang.model.element.Element;

/**
 * @author xjs
 *         on  2018/1/14
 *         desc:
 */

public interface ITypeTransform {

    boolean accept(Element element);

    CharSequence transform(Element element);
}
