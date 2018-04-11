package com.helloyuyu.routerwrapper.compiler.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author xjs
 *         on 2018/1/18
 *         desc: 子集的工具
 */

public class SubsetUtils {

    /**
     * 递归获取 start 到 end(包括) 整数的 所有非空子集
     *
     * @param start ；
     * @param end   ；
     * @return ；
     */
    public static Set<int[]> recursionGetCombination(int start, int end) {
        Set<int[]> result = new LinkedHashSet<>();
        if (start >= end) {
            result.add(new int[]{start});
            return result;
        } else if (end - start == 1) {
            result.add(new int[]{start});
            result.add(new int[]{end});
            result.add(new int[]{start, end});
            return result;
        }
        int mid = (end + start) / 2;
        Set<int[]> startList = recursionGetCombination(start, mid - 1);
        Set<int[]> endList = recursionGetCombination(mid, end);
        result.addAll(startList);
        result.addAll(endList);
        for (int[] startInts : startList) {
            for (int[] endInts : endList) {
                result.add(mixInts(startInts, endInts));
            }
        }
        return result;
    }

    private static int[] mixInts(int[] startInts, int[] endInts) {
        int[] result = new int[startInts.length + endInts.length];
        System.arraycopy(startInts, 0, result, 0, startInts.length);
        System.arraycopy(endInts, 0, result, startInts.length, endInts.length);
        Arrays.sort(result);
        return result;
    }


    public static ArrayList<ArrayList<Integer>> getSubsets2(ArrayList<Integer> set) {
        ArrayList<ArrayList<Integer>> allsubsets = new ArrayList<ArrayList<Integer>>();
        //how many sub sets
        int max = 1 << set.size();
        for (int i = 0; i < max; i++) {
            int index = 0;
            int k = i;
            ArrayList<Integer> s = new ArrayList<Integer>();
            while (k > 0) {
                if ((k & 1) > 0) {
                    s.add(set.get(index));
                }
                k >>= 1;
                index++;
            }
            allsubsets.add(s);
        }
        return allsubsets;
    }
}
