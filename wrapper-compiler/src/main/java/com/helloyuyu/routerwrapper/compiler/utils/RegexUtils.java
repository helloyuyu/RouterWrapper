package com.helloyuyu.routerwrapper.compiler.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xjs
 *         on 2018/2/7
 *         desc:
 */

public class RegexUtils {

    private final static String PACKAGE_NAME_PATTERN = "^([a-zA-Z]+[.][a-zA-Z]+)[.]*.*";

    public static boolean isPackageName(String targetStr) {
        return regular(targetStr, PACKAGE_NAME_PATTERN);
    }

    /**
     *    * 匹配是否符合正则表达式pattern 匹配返回true
     *    * @param str 匹配的字符串
     *    * @param pattern 匹配模式
     *    * @return boolean
     *    
     */
    private static boolean regular(String str, String pattern) {
        if (null == str || str.trim().length() <= 0) {
            return false;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
