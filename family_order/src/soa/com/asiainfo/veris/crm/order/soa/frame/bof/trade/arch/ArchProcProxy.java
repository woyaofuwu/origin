package com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ArchProcProxy implements InvocationHandler
{
    private IArchProc archProc;

    public ArchProcProxy(IArchProc archProcImpl)
    {
        this.archProc = archProcImpl;
    }

    public static IArchProc getInstance(String tabName) throws Exception
    {
        return ArchProcConfig.getArchProcInstance(tabName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        return method.invoke(this.archProc, args);
    }

}
