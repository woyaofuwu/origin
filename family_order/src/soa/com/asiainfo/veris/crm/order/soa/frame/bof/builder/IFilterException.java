
package com.asiainfo.veris.crm.order.soa.frame.bof.builder;

import com.ailk.common.data.IData;

public interface IFilterException
{
    public IData transferException(Throwable e, IData input) throws Exception;
}
