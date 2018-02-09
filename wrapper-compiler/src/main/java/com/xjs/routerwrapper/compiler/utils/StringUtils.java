package com.xjs.routerwrapper.compiler.utils;

public class StringUtils {
    /**
     * 首字母大写
     *
     * @param srcStr ;
     * @return ;
     */
    public static String firstCharacterToUpper(String srcStr) {
        if (srcStr == null) {
            return "";
        }
        if (srcStr.length() == 1) {
            return srcStr.toUpperCase();
        }
        return srcStr.substring(0, 1).toUpperCase() + srcStr.substring(1);
    }

    public static String firstCharacterToLow(String srcStr) {
        if (srcStr == null) {
            return "";
        }
        if (srcStr.length() == 1) {
            return srcStr.toLowerCase();
        }
        return srcStr.substring(0, 1).toLowerCase() + srcStr.substring(1);
    }


    /**
     * 替换字符串并让它的下一个字母为大写
     *
     * @param srcStr ;
     * @param org    ;
     * @param ob     ;
     * @return ;
     */
    public static String replaceUnderlineAndFirstToUpper(String srcStr, String org, String ob) {
        String newString = "";
        int first = 0;
        while (srcStr.contains(org)) {
            first = srcStr.indexOf(org);
            if (first != srcStr.length()) {
                newString = newString + srcStr.substring(0, first) + ob;
                srcStr = srcStr.substring(first + org.length(), srcStr.length());
                srcStr = firstCharacterToUpper(srcStr);
            }
        }
        newString = newString + srcStr;
        return newString;
    }
    public static boolean isNotEmpty(String input){
        return null != input && !input.isEmpty();
    }
}