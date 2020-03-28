package com.xycode.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * ClassName: TestUnsafe
 *
 * @Author: xycode
 * @Date: 2020/2/25
 * @Description: this is description of the TestUnsafe class
 **/
public class TestUnsafe {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        //通过反射方式获取Unsafe的theUnsafe域
        Field unsafeField= Unsafe.class.getDeclaredField("theUnsafe");
        System.out.println(unsafeField);

        //theUnsafe是private的,所以这里需要设置访问权限
        unsafeField.setAccessible(true);

        //根据theUnsafe获得Unsafe对象,注意这里这里的theUnsafe是static的,所以用get(null)
        Unsafe unsafe= (Unsafe) unsafeField.get(null);

        //使用Unsafe的CAS操作
        Student student=new Student();
        //获取id,name在对象中的偏移量,以便后续CAS使用
        long idOffset=unsafe.objectFieldOffset(Student.class.getDeclaredField("id"));
        long nameOffset=unsafe.objectFieldOffset(Student.class.getDeclaredField("name"));
        unsafe.compareAndSwapInt(student,idOffset,0,1234);
        unsafe.compareAndSwapObject(student,nameOffset,null,"徐岩");

        //测试看是否赋值成功
        System.out.println(student);
    }


    static class Student{
        volatile int id;
        volatile String name;

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name=" + name +
                    '}';
        }
    }

}
