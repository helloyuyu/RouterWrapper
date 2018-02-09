package com.xjs.routerwrapper.compiler.utils;


import com.xjs.routerwrapper.compiler.Constants;

/**
 * @author xjs
 *         on 2018/1/17
 *         desc:
 */

public enum RouteTypes {
    /**
     * support activity
     */
    SUPPORT_ACTIVITY(Constants.ANDROID_SUPPORT_ACTIVITY_CLASS_NAME),
    /**
     * activity
     */
    ACTIVITY(Constants.ANDROID_ACTIVITY_CLASS_NAME);

    private String className;

    RouteTypes(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
