package com.xycode.actor;

public class ActiveObjectFactory {
	public static ActiveObject createActiveObject() {
		return new ActiveObjectImpl();
	}
}
