package com.xycode.observer;

public class Main {
    public static void main(String[] args) {
        final Subject subject=new Subject();
        subject.attach(new BinaryObserver(subject));

        subject.attach(new OctalObserver(subject));

        subject.setState(100);//BinaryObserver与OctalObserver会监听到这个set,并打印信息
    }
}






