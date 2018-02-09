package com.xjs.routerwrapper.compiler.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @author xjs
 *         on 2018/2/8
 *         desc:
 */

public class CollectionUtils {

    public static boolean isNotEmpty(Collection conllection) {
        return null != conllection && !conllection.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return null != map && !map.isEmpty();
    }
}
