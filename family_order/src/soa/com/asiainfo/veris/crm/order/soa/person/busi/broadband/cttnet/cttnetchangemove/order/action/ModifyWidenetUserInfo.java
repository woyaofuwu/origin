
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.action;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.requestdata.CttBroadbandMoveReqData;

public class ModifyWidenetUserInfo implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        CttBroadbandMoveReqData cttBroadbandcreateRD = (CttBroadbandMoveReqData) btd.getRD();
        UserTradeData userData = cttBroadbandcreateRD.getUca().getUser();
        String userId = userData.getUserId();
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            UserTradeData userTradeData = new UserTradeData(userInfo);
            MainTradeData mainData = btd.getMainTradeData();
            userTradeData.setCityCode(mainData.getCityCode());
            userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(userData.getSerialNumber(), userTradeData);
        }
    }

}
