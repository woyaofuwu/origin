
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.fee;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserFeeIntf
{

    /**
     * 查询用户预存信息
     * 
     * @param bc
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserForegiftInfo(IBizCommon bc, String userId, String routeId) throws Exception
    {
        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.ROUTE_EPARCHY_CODE, routeId);

        IDataset userForegiftList = CSViewCall.call(bc, "CS.UserForegiftInfoQrySVC.qryUserForegiftByUserId", svcData);

        double foregift = 0;

        if (IDataUtil.isNotEmpty(userForegiftList))
        {
            for (int i = 0, row = userForegiftList.size(); i < row; i++)
            {
                foregift += (Double.valueOf(userForegiftList.getData(i).getString("MONEY", "0")) / 100.00);
            }
        }

        IData retData = new DataMap();
        retData.put("FOREGIFT", String.valueOf(foregift));

        return retData;
    }

    /**
     * 查询用户欠费信息
     * 
     * @param bc
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IData qryUserOweFeeInfo(IBizCommon bc, String userId, String routeId) throws Exception
    {
        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.ROUTE_EPARCHY_CODE, routeId);

        IData oweFeeData = CSViewCall.callone(bc, "CS.UserOwenInfoQrySVC.getOweFeeByUserId", svcData);

        return oweFeeData;
    }
}
