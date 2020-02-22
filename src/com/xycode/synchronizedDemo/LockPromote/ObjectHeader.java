package com.xycode.synchronizedDemo.LockPromote;

import org.openjdk.jol.info.ClassLayout;

import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ClassName: Main
 *
 * @Author: xycode
 * @Date: 2019/11/28
 * @Description: this is description of the Main class
 **/
public class ObjectHeader {
    static class Obj{
        int i=0;
//        byte[] array=new byte[100];
    }

    public static String getObjectHeader(Object o){
        ByteOrder order=ByteOrder.nativeOrder();//字节序
        String table=ClassLayout.parseInstance(o).toPrintable();
        Pattern p=Pattern.compile("(0|1){8}");
        Matcher matcher=p.matcher(table);
        List<String> header=new ArrayList<>();
        while(matcher.find()){
            header.add(matcher.group());
        }
        //小端机器,需要反过来遍历
        StringBuilder sb=new StringBuilder();
        if(order.equals(ByteOrder.LITTLE_ENDIAN)){
            Collections.reverse(header);
        }
        for(String s:header){
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * 针对64bit jvm的解析对象头函数
     * 在64bit jvm中,对象头有两个部分: Mark Word和Class Pointer, Mark Word占8字节,Class Pointer占4字节
     * @param s 对象头的二进制形式字符串(每8位,使用一个空格分开)
     */
    public static void parseObjectHeader(String s){
        String[] tmp=s.split(" ");
        System.out.print("Class Pointer: ");
        for(int i=0;i<4;++i){
            System.out.print(tmp[i]+" ");
        }
        System.out.println("\nMark Word:");
        if(tmp[11].charAt(5)=='0'&&tmp[11].substring(6).equals("01")){//0 01无锁状态,不考虑GC标记的情况
            //notice: 无锁情况下mark word的结构: unused(25bit) + hashcode(31bit) + unused(1bit) + age(4bit) + biased_lock_flag(1bit) + lock_type(2bit)
            //      hashcode只需要31bit的原因是: hashcode只能大于等于0,省去了负数范围,所以使用31bit就可以存储
            System.out.print("\thashcode (31bit): ");
            System.out.print(tmp[7].substring(1)+" ");
            for(int i=8;i<11;++i) System.out.print(tmp[i]+" ");
            System.out.println();
        }else if(tmp[11].charAt(5)=='1'&&tmp[11].substring(6).equals("01")){//1 01,即偏向锁的情况
            //notice: 对象处于偏向锁的情况,其结构为: ThreadID(54bit) + epoch(2bit) + unused(1bit) + age(4bit) + biased_lock_flag(1bit) + lock_type(2bit)
            //      这里的ThreadID是持有偏向锁的线程ID, epoch: 一个偏向锁的时间戳,用于偏向锁的优化
            System.out.print("\tThreadID(54bit): ");
            for(int i=4;i<10;++i) System.out.print(tmp[i]+" ");
            System.out.println(tmp[10].substring(0,6));
            System.out.println("\tepoch: "+tmp[10].substring(6));
        }else{//轻量级锁或重量级锁的情况,不考虑GC标记的情况
            //notice: JavaThread*(62bit,include zero padding) + lock_type(2bit)
            //      此时JavaThread*指向的是 栈中锁记录/重量级锁的monitor
            System.out.print("\tjavaThread*(62bit,include zero padding): ");
            for(int i=4;i<11;++i) System.out.print(tmp[i]+" ");
            System.out.println(tmp[11].substring(0,6));
            System.out.println("\tLockFlag (2bit): "+tmp[11].substring(6));
            System.out.println();
            return;
        }
        System.out.println("\tage (4bit): "+tmp[11].substring(1,5));
        System.out.println("\tbiasedLockFlag (1bit): "+tmp[11].charAt(5));
        System.out.println("\tLockFlag (2bit): "+tmp[11].substring(6));

        System.out.println();
    }


    public static void main(String[] args) {
        /**
         * Mark Word 占 8 字节
         * Class Pointer 占 4 字节
         *
         * 偏向锁标志位(1bit,1表示有偏向锁)+锁状态位(2bit)
         * (1)01: 偏向锁
         * (0)01: 无锁
         *
         * 00: 轻量级锁
         * 10: 重量级锁
         *
         * 11: GC标记,当为该值是,偏向锁标志位必定为0
         */
        Obj o=new Obj();
//        测试synchronized锁升级的情况,若要立即查看测试情况,需要禁止偏向锁延迟: -XX:BiasedLockingStartupDelay=0
//        开启偏向锁: -XX:+UseBiasedLocking=0, jdk6开始就默认开启了,不开启就是先使用轻量级锁
//        System.out.println(o.hashCode());//notice: 调用hashcode后就没有偏向锁了...(因为bit位置被占了)

        //main线程,偏向锁
        synchronized (o){
            parseObjectHeader(getObjectHeader(o));
        }
        //轻量级锁,因为下面的线程又占用了o
        new Thread(()->{
            synchronized (o){
                parseObjectHeader(getObjectHeader(o));
            }
        }).start();

        //重量级锁
//        for(int i=0;i<2;++i)//线程数大于1时(交错执行),会升级成重量级锁
//            new Thread(()->{
//                synchronized (o){
//                    parseObjectHeader(getObjectHeader(o));
//                }
//            }).start();


        //测试hashcode,并且会导致不能偏向锁
//        parseObjectHeader(getObjectHeader(o));
//        System.out.println(o.hashCode());
//        System.gc();//会增加一次分代年龄,连续调用好像只能起一次效果
//        parseObjectHeader(getObjectHeader(o));

    }
}
