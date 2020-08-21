
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeLbsUserElement extends ChangeUserElement
{
    private boolean changeFlag = true;

    private String phone;

    private String info;

    private String addr;

    public IData getTradeUserExtendData() throws Exception
    {
        IData userInfoExtend = super.getTradeUserExtendData();

        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        phone = paramData.getString("MGR_PHONE", "");
        info = paramData.getString("MGR_INFO", "");
        addr = paramData.getString("ADDRESS", "");

        String userId = reqData.getUca().getUserId();
        IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");

        if (phone.equals(userInfo.getString("RSRV_STR8", "")) && info.equals(userInfo.getString("RSRV_STR7", "")) && addr.equals(userInfo.getString("RSRV_STR9", "")))
        {
            changeFlag = false;
        }
        else
        {
            userInfoExtend.putAll(userInfo);
            userInfoExtend.put("RSRV_STR8", phone);
            userInfoExtend.put("RSRV_STR7", info);
            userInfoExtend.put("RSRV_STR9", addr);
            userInfoExtend.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }

        return userInfoExtend;

    }

    @Override
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData tradeData = bizData.getTrade();

        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        tradeData.put("RSRV_STR8", paramData.getString("MGR_PHONE", ""));
        tradeData.put("RSRV_STR7", paramData.getString("MGR_INFO", ""));
        tradeData.put("RSRV_STR9", paramData.getString("ADDRESS", ""));
    }
}
