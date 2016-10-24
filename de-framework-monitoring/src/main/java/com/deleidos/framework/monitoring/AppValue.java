package com.deleidos.framework.monitoring;

import com.deleidos.framework.monitoring.response.App;

public interface AppValue<T> {
	public void addValueOf(App a);
	public T getValue();
}
