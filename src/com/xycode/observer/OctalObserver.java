package com.xycode.observer;

public class OctalObserver extends Observer {

    public OctalObserver(Subject subject){
        this.subject=subject;
    }
    @Override
    public void update() {
        System.out.println("OctalObserver : "+Integer.toOctalString(subject.getState()));
    }
}
