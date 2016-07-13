package com.deleidos.framework.operators.datamodel;

import com.deleidos.framework.operators.datamodel.InitializationException;

public interface Initializable {
    public void initialize() throws InitializationException;
    public void dispose();
}
