
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.Msisdn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class MsisdnInfoIntf
{
    /**
     * 通过成员用户ID,集团用户ID查询号码已经订购该集团产品的服务，资费，资源信息
     * 
     * @param bc
     * @param userId
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserMsisdnInfoBySerialnumber(IBizCommon bc, String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        return CSViewCall.call(bc, "CS.MsisdnInfoQrySVC.getCrmMsisonBySerialnumber", inparam);
    }

}
