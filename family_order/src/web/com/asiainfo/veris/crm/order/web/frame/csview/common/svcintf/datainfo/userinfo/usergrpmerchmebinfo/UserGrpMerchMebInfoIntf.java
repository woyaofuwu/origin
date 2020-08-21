
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.usergrpmerchmebinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserGrpMerchMebInfoIntf
{

    /**
     * 通过成员USERID,成员号码SERIAL_NUMBER和集团的EC_USER_ID查询成员订购的bboss订购信息
     * 
     * @param bc
     * @param ecUserId
     * @param userId
     * @param serialNumber
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpMerchMebInfosByEcUserIdAndUserIdSerialNumber(IBizCommon bc, String ecUserId, String userId, String serialNumber, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("EC_USER_ID", ecUserId);
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        return CSViewCall.call(bc, "CS.UserGrpMerchMebInfoQrySVC.qryMerchMebInfoByEcUserIdSnUserId", inparam);
    }

}
