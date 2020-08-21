
package com.asiainfo.veris.crm.order.soa.frame.bof.builder.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.ParamFilterConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.RequestBuilderConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BuilderProxy implements InvocationHandler
{
    public static IBuilder getInstance(String tradeTypeCode, String orderTypeCode, IData input) throws Exception
    {
        IFilterIn in = ParamFilterConfig.getInParamFilter(input.getString("X_TRANS_CODE"), input);
        if (in != null)
        {
            in.transferDataInput(input);
        }
        return RequestBuilderConfig.getRequestBuilder(tradeTypeCode, orderTypeCode, input);
    }

    private IBuilder buildImpl;

    public BuilderProxy(IBuilder builderImpl)
    {
        this.buildImpl = builderImpl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        BaseReqData rd = (BaseReqData) method.invoke(this.buildImpl, args);
        return rd;
    }
}
