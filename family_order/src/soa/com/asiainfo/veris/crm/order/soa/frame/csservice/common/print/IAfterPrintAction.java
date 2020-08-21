
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.print;

import com.ailk.common.data.IData;

public interface IAfterPrintAction
{
    public abstract void action(IData param) throws Exception;
}
