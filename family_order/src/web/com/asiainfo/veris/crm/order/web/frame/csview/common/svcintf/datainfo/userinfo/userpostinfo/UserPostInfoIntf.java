
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.userpostinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserPostInfoIntf
{
    /**
     * 通过ID和ID_TYPE查询邮寄信息
     * 
     * @param bc
     * @param id
     * @param idType
     *            0客户 1用户 2账户
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryPostInfosByIdAndIdType(IBizCommon bc, String id, String idType, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ID", id);
        inparam.put("ID_TYPE", idType);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserPostInfoQrySVC.qryPostInfoForGrp", inparam);
    }

}
