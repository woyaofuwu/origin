
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UStaticInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = -8888231864234237506L;

    public static final String getStaticValue(IData iData) throws Exception
    {
        IDataUtil.chkParam(iData, "TYPE_ID");

        return UStaticInfoQry.getStaticValue(iData.getString("TYPE_ID"), iData.getString("DATA_ID"));
    }
}
