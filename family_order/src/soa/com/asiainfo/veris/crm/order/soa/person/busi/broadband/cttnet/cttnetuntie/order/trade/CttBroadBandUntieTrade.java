
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetuntie.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetuntie.order.requestdata.CttBroadBandUntieReqData;

public class CttBroadBandUntieTrade extends BaseTrade implements ITrade
{

    private void checkBiz(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        CttBroadBandUntieReqData reqData = (CttBroadBandUntieReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();
        IDataset userAccessInfo = BroadBandInfoQry.qryAccessByUserId(ucaData.getUserId());
        if (userAccessInfo.isEmpty())
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_17, ucaData.getSerialNumber());
        }

        IDataset userSvcState = UserSvcStateInfoQry.queryUserSvcStateInfo(ucaData.getUserId());
        if (userSvcState == null || userSvcState.size() == 0)
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_28, ucaData.getSerialNumber());
        }
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 规则校验

        // checkBiz(btd);

        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRemark("用户宽带解绑操作");// 备注
        mainTradeData.setNetTypeCode("11");
        // 设置网别编码11：宽带用户
        mainTradeData.setSubscribeType("300");// 走PBOSS流程
        mainTradeData.setPfType("300");// 走PBOSS流程
        mainTradeData.setOlcomTag("1");// 发指令流程

    }

}
