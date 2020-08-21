
package com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement.order.requestdata.GetGiftOfStudyImbursementRequestData;

public class GetGiftOfStudyImbursementTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        GetGiftOfStudyImbursementRequestData reqData = (GetGiftOfStudyImbursementRequestData) btd.getRD();

        String sysdate = SysDateMgr.getSysDate();
        String endDate = SysDateMgr.END_TIME_FOREVER;
        String giftsInfo = reqData.getGiftsInfo();
        IDataset gifts = new DatasetList(giftsInfo);
        if (IDataUtil.isEmpty(gifts))
        {
            // 请选择兑换礼品
            CSAppException.apperr(CrmUserException.CRM_USER_1064);
        }
        for (int i = 0; i < gifts.size(); i++)
        {
            IData tempData = new DataMap();
            tempData = (IData) gifts.get(i);
            OtherTradeData tradeTD = new OtherTradeData();
            tradeTD.setUserId(reqData.getUca().getUser().getUserId());
            tradeTD.setRsrvValueCode("SI");
            tradeTD.setRsrvValue(tempData.getString("TRADE_ID"));
            tradeTD.setInstId(SeqMgr.getInstId());
            tradeTD.setRsrvStr1(tempData.getString("RULE_ID"));// 兑换规则ID，可能就是礼品编码
            tradeTD.setRsrvStr3(tempData.getString("GOODS_NAME"));// 礼品名称
            tradeTD.setRsrvStr4(tempData.getString("PARA_CODE1"));// TD_S_COMMPARA.PARA_CODE1 礼品编码
            tradeTD.setRsrvStr5(tempData.getString("PARA_CODE3"));// TD_S_COMMPARA.PARA_CODE3 礼品名称
            tradeTD.setRsrvStr6(tempData.getString("GOODS_STATE"));// 礼品状态
            tradeTD.setRsrvNum1(tempData.getString("ACTION_COUNT"));// 礼品个数
            tradeTD.setStartDate(sysdate);
            tradeTD.setEndDate(endDate);
            tradeTD.setModifyTag("0");
            tradeTD.setRemark("全球通积分助学礼品领取");
            btd.add(reqData.getSerialNumber(), tradeTD);
        }
    }
}
