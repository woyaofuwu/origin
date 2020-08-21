
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.Msisdn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.Msisdn.MsisdnInfoIntf;

public class MsisdnInfoViewUtil
{
    public static IDataset qryUserMsisdnInfo(IBizCommon bc, String serialNumber) throws Exception
    {
       return  MsisdnInfoIntf.qryUserMsisdnInfoBySerialnumber(bc, serialNumber);
    }
   
}
