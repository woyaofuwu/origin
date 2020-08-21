
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeNetZxInputUser extends ChangeUserElement
{

    private boolean paramChage = false;

    private IData paramData = null;

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        dealProductParamInfo();

    }

    public void dealProductParamInfo() throws Exception
    {
        // 参数登记到USER表中
        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        paramData = productParam;
        reqData.cd.putProductParamList(reqData.getUca().getProductId(), new DatasetList());

        UserTradeData grpUserInfo = reqData.getUca().getUser();

        String oldMgrInfo = grpUserInfo.getRsrvStr7();// 管理员信息
        String oldMgrPhone = grpUserInfo.getRsrvStr8();// 管理员电话
        String oldAddress = grpUserInfo.getRsrvStr9();// 联系地址

        String newMgrInfo = paramData.getString("DETMANAGERINFO", "");// 管理员信息
        String newMgrPhone = paramData.getString("DETMANAGERPHONE", "");// 管理员电话
        String newAddress = paramData.getString("DETLINKPHONE", "");// 联系地址

        if (newMgrInfo.equals(oldMgrInfo) && newMgrPhone.equals(oldMgrPhone) && newAddress.equals(oldAddress))
        {
            return;
        }
        paramChage = true;
    }

    public IData getTradeUserExtendData() throws Exception
    {

        IData userExtenData = super.getTradeUserExtendData();
        if (paramChage)
        {
            String newMgrInfo = paramData.getString("DETMANAGERINFO", "");// 管理员信息
            String newMgrPhone = paramData.getString("DETMANAGERPHONE", "");// 管理员电话
            String newAddress = paramData.getString("DETLINKPHONE", "");// 联系地址
            userExtenData.put("RSRV_STR7", newMgrInfo);
            userExtenData.put("RSRV_STR8", newMgrPhone);
            userExtenData.put("RSRV_STR9", newAddress);
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        return userExtenData;
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        if (paramChage)
        {
            IData data = bizData.getTrade();

            data.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));// 管理员信息
            data.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));// 管理员电话
            data.put("RSRV_STR9", paramData.getString("DETLINKPHONE", ""));// 联系地址
        }
    }

}
