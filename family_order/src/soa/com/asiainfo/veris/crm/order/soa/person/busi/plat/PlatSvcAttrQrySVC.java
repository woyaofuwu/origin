
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatInfoQry;

public class PlatSvcAttrQrySVC extends CSBizService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset PlatSvcAttrAllQry(IData data) throws Exception
    {

        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String bizTypeCode = data.getString("BIZ_TYPE_CODE", "");
        String serviceId = data.getString("SERVICE_ID", "");
        String infoCode = data.getString("INFO_CODE", "");
        IDataset result = UserPlatInfoQry.getUserPlatAttrInfos(serialNumber, bizTypeCode, serviceId, infoCode);
        return result;

    }

}
