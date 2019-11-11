package com.xycode.synchronizedDemo;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * ClassName: Test
 *
 * @Author: xycode
 * @Date: 2019/11/9
 * @Description: this is description of the Test class
 **/
public class Test {
    static class test{
        int i=0;
        byte[] array=new byte[100];
    }
    public static void main(String[] args) {
        System.out.println(ClassLayout.parseInstance(new test()).toPrintable());

    }
}
