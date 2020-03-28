package com.xycode.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * ClassName: UnsafeUtils
 *
 * @Author: xycode
 * @Date: 2020/2/25
 * @Description: this is description of the UnsafeUtils class
 **/
public class UnsafeUtils {
    private static final Unsafe unsafe;
    static{
        //通过反射方式获取Unsafe的theUnsafe域
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            //theUnsafe是private的,所以这里需要设置访问权限
            unsafeField.setAccessible(true);

            //根据theUnsafe获得Unsafe对象,注意这里这里的theUnsafe是static的,所以用get(null)
            unsafe= (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Unsafe getUnsafe(){
        return unsafe;
    }
}
