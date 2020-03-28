package com.xycode.executor.myThreadPool;

/**
 * InterfaceName: MyRejectStrategy
 *
 * @Author: xycode
 * @Date: 2020/2/26
 * @Description: this is description of the interface
 **/
@FunctionalInterface
public interface MyRejectStrategy<T> {
    //拒绝策略,需要拿到任务队列与待添加的任务
    void reject(MyBlockingQueue<T> queue,T element) throws Exception;
}
