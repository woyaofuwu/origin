
package com.asiainfo.veris.crm.order.soa.frame.bof.builder;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public interface IBuilder
{
    public BaseReqData buildRequestData(IData param) throws Exception;
}
