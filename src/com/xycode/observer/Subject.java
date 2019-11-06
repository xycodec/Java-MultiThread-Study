package com.xycode.observer;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private List<Observer> observers=new ArrayList<>();
    private int state;

    public void setState(int state) {
        if(state==this.state) return;
        this.state = state;
        notifyAllObserver();
    }

    public int getState() {
        return state;
    }

    public void attach(Observer observer){
        observers.add(observer);
    }

    private void notifyAllObserver(){
        observers.stream().forEach(Observer::update);
        //函数式编程,对每个Observer都执行update方法
    }

}
