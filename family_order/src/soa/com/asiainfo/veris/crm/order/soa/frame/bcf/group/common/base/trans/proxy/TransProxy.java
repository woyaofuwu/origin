
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.config.RequestTransConfig;

public class TransProxy implements InvocationHandler
{
    public static ITrans getInstance(IData idata) throws Exception
    {
        String xTransCode = idata.getString("X_TRANS_CODE", "");
        return RequestTransConfig.getBaseTrans(xTransCode, idata);
    }

    private ITrans transImpl;

    public TransProxy(ITrans transImpl)
    {
        this.transImpl = transImpl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {

        return method.invoke(this.transImpl, args);
    }
}
