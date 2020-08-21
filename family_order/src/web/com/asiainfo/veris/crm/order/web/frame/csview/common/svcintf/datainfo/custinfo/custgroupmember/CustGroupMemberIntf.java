
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.custinfo.custgroupmember;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CustGroupMemberIntf
{

    /**
     * 通过手机号码查询号码的资料归属集团
     * 
     * @param bc
     * @param serialNumber
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpMebsBySN(IBizCommon bc, String serialNumber, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.GrpMebInfoQrySVC.queryGrpMebBySN", inparam);
    }

}
