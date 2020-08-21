
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat.credit;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatMemStatusChgSVC extends GroupBatService
{

    public static final long serialVersionUID = 1L;

    public static final String SERVICE_NAME = "SS.MemberStateChgSVC.crtTrade";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {
        String userIdA = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userIdA);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serialNumber);
        chkMemberUCABySerialNumber(inparam);

    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("DEAL_FLAG", IDataUtil.getMandaData(condData, "DEAL_FLAG"));
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

    }

}
