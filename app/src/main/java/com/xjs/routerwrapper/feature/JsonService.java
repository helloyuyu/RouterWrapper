package com.xjs.routerwrapper.feature;

import java.lang.reflect.Type;

/**
 * @author xjs
 *         on  2018/2/4
 *         desc:
 */

public interface JsonService {

    String object2Json(Object instance);

    <T> T parseObject(String input, Type clazz);
}
