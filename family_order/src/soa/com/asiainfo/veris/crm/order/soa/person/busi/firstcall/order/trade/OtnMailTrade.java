
package com.asiainfo.veris.crm.order.soa.person.busi.firstcall.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.firstcall.order.requestdata.OtnMailRequestData;

public class OtnMailTrade extends BaseTrade implements ITrade
{

    @SuppressWarnings("unchecked")
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        OtnMailRequestData reqData = (OtnMailRequestData) btd.getRD();
        createTradeService(btd, reqData);
    }

    private void createTradeService(BusiTradeData<BaseTradeData> btd, OtnMailRequestData reqData) throws Exception
    {

        SvcTradeData newSvc = new SvcTradeData();

        newSvc.setUserId(reqData.getUserId());
        newSvc.setUserIdA("-1");
        newSvc.setInstId(SeqMgr.getInstId());
        newSvc.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newSvc.setMainTag("0");
        newSvc.setCampnId("0");
        newSvc.setStartDate(SysDateMgr.getSysTime());
        newSvc.setEndDate(SysDateMgr.END_DATE_FOREVER);
        newSvc.setPackageId(reqData.getPackageId());
        newSvc.setProductId(reqData.getProductId());
        newSvc.setElementId(reqData.getElementId());
        newSvc.setElementType("S");
        newSvc.setRemark("开通139邮箱电子账单服务");
        btd.add(reqData.getSerialNumber(), newSvc);

    }

}
