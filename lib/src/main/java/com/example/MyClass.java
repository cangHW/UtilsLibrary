package com.example;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MyClass {

    public static void main(String[] args){
//        BigDecimal decimal=new BigDecimal("0.6");
//        BigDecimal decimal1=new BigDecimal("0.4");
//        BigDecimal bigDecimal=decimal.divide(decimal1);
//        BigDecimal bigDecimal1=bigDecimal.setScale(2,RoundingMode.HALF_UP);
//        System.out.println(bigDecimal1.toString());
//        Object s=0.2;
//        String ss=(String) s;
//        System.out.println(ss);
        String ss="ss&12&AA";
        String[] s=ss.split("&");
        for (String s1:s){
            System.out.println(s1);
        }
    }
}
