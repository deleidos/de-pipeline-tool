package com.deleidos.framework.monitoring;

import com.deleidos.framework.monitoring.response.AppsResponse.AppWrapper.App;

public interface AppValue<T> {
	public void addValueOf(App a);
	public T getValue();
}
