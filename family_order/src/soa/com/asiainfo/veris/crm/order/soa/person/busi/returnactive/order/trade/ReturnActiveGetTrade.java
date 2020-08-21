
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ReturnActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveGetReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveGetCardData;

public class ReturnActiveGetTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ReturnActiveGetReqData reqData = (ReturnActiveGetReqData) bd.getRD();
        UcaData uca = reqData.getUca();

        List<ReturnActiveGetCardData> cardList = reqData.getCardList();
        if (cardList.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "领取的卡列表");
        }

        MainTradeData mainTD = bd.getMainTradeData();
        mainTD.setRsrvStr1(String.valueOf(cardList.size()));

        for (int i = 0, size = cardList.size(); i < size; i++)
        {
            ReturnActiveGetCardData cardData = cardList.get(i);

            OtherTradeData otherTD = new OtherTradeData();
            otherTD.setRsrvValueCode("RAGGCARD");
            otherTD.setRsrvValue("真情回馈百分百刮刮卡");
            otherTD.setUserId(uca.getUserId());
            otherTD.setStartDate(reqData.getAcceptTime());
            otherTD.setEndDate(SysDateMgr.getTheLastTime());
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setRsrvStr1(cardData.getGgCardNo());
            otherTD.setRsrvStr2(bd.getTradeId());
            otherTD.setProcessTag("0");// 未兑奖
            otherTD.setRemark(cardData.getRemark());
            otherTD.setInstId(SeqMgr.getInstId());
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

            bd.add(uca.getSerialNumber(), otherTD);
            // 调资源接口
        }

        int iHaveNum = 0;
        IDataset userOtherList = UserOtherInfoQry.getUserOther(uca.getUserId(), "RETURNACTIVE");
        for (int i = 0, size = userOtherList.size(); i < size; i++)
        {
            IData userOther = userOtherList.getData(i);
            iHaveNum += userOther.getInt("RSRV_STR1");
            OtherTradeData delOtherTD = new OtherTradeData(userOther);
            delOtherTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delOtherTD.setEndDate(reqData.getAcceptTime());
            bd.add(uca.getSerialNumber(), delOtherTD);
        }

        iHaveNum = iHaveNum - cardList.size();
        if (iHaveNum < 0)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_2);
        }
        OtherTradeData returnActiveTD = new OtherTradeData();
        returnActiveTD.setUserId(uca.getUserId());
        returnActiveTD.setRsrvValueCode("RETURNACTIVE");
        returnActiveTD.setRsrvValue("真情回馈百分百");
        returnActiveTD.setRsrvStr1(String.valueOf(iHaveNum));
        returnActiveTD.setRsrvStr2(bd.getTradeId());
        returnActiveTD.setStartDate(reqData.getAcceptTime());
        returnActiveTD.setEndDate(SysDateMgr.getTheLastTime());
        returnActiveTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        returnActiveTD.setStaffId(CSBizBean.getVisit().getStaffId());
        returnActiveTD.setDepartId(CSBizBean.getVisit().getDepartId());
        returnActiveTD.setInstId(SeqMgr.getInstId());
        returnActiveTD.setRemark("领卡新增记录");
        bd.add(uca.getSerialNumber(), returnActiveTD);
    }

}
