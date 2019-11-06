package com.xycode.observer;

public class BinaryObserver extends Observer{

    public BinaryObserver(Subject subject){
        this.subject=subject;

    }
    @Override
    public void update() {
        System.out.println("BinaryObserver : "+Integer.toBinaryString(subject.getState()));
    }
}
