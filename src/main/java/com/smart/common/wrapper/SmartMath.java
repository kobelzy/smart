package com.smart.common.wrapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 2017/1/5.
 */
public class SmartMath {
    /**
     * 全排列，遍历source集合的全部取法
     * Param: 
     * Return: 
     * Created by licheng on 2017/1/5.
     */
    public List<LinkedList<String>> combind(String... source){
        List<LinkedList<String>> result = new LinkedList<LinkedList<String>>();
        int m = 0;
        while(m < source.length){
            for(int i = m;i < source.length;i++){
                LinkedList<String> e = new LinkedList<String>();
                int j = m;
                while(j <= i){
                    e.add(source[j]);
                    j++;
                }
                result.add(e);
            }
            m++;
        }
        return result;
    }
}
