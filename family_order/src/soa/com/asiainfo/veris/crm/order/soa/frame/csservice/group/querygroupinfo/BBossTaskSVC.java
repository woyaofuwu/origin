
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class BBossTaskSVC extends CSBizService
{
    /**
     * chenyi 2014-6-16 业务冲抵 前台直接调iboss实施接口
     * 
     * @param httpStr
     * @return
     * @throws Exception
     */
    public static IDataset callIBOSS(IData httpStr) throws Exception
    {
        String kindId = httpStr.getString("KIND_ID");
        return IBossCall.dealInvokeUrl(kindId, "IBOSS7", httpStr);
    }

}
