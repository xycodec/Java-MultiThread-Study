package com.xycode.synchronizedDemo;

public class LockStatic {
    public static int cnt=0;
    public static int cnt2=0;
    public static int cnt3=0;

    private static Object staticLock=new Object();
    public void inc(){
        synchronized (staticLock){
            ++cnt;
        }
    }

    public synchronized static void inc2(){
        ++cnt2;
    }

    public static void inc3(){
        synchronized (LockStatic.class){
            ++cnt3;
        }
    }

    public static void main(String[] args) {
        Thread[] t=new Thread[1000];
        //因为只有一个LockObject实例,所以synchronized起到了同步的作用
        LockStatic lockStatic=new LockStatic();
        LockStatic lockStatic2=new LockStatic();

        LockObject lockObject=new LockObject();
        LockObject lockObject2=new LockObject();
        for(int i=0;i<t.length;++i){
            int finalI = i;
            t[i]=new Thread(){
                @Override
                public void run() {
                    if(finalI %2==0){
                        lockStatic.inc();
                        lockObject.inc();
                    }else{
                        lockStatic2.inc();
                        lockObject2.inc();
                    }
                    inc2();
                    inc3();
                    System.out.println("cnt="+cnt+", cnt2="+cnt2+", cnt3="+cnt3);
                }
            };
        }
        for(int i=0;i<t.length;++i) {
            t[i].start();
        }
        //休眠一会儿,不如main线程可能会先于其它线程 退出
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(cnt);
        System.out.println(cnt2);
        System.out.println(cnt3);

        //这里的synchronized修饰方法或synchronized(this){}在线程调用多个实例的情况下就线程不安全了
        System.out.println(LockObject.cnt);
        /**
         * 最后输出:
         * 1000
         * 1000
         * 1000
         * 997
         * 注:最后这个输出也可能会是1000,不要被迷惑了...
         */
    }
}



