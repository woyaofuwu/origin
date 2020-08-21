
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.order.requestdata.ProsecutionTradeReqData;

public class ProsecutionTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ProsecutionTradeReqData reqData = (ProsecutionTradeReqData) btd.getRD();
        geneTableTradeMain(btd, reqData);
        geneTradeProduct(btd, reqData);
    }

    private void geneTableTradeMain(BusiTradeData btd, ProsecutionTradeReqData reqData) throws Exception
    {
        MainTradeData mainTrade = btd.getMainTradeData();
        mainTrade.setRsrvStr1(reqData.getProsecutionNumber());
        mainTrade.setRsrvStr2(reqData.getUca().getSerialNumber());
        mainTrade.setRsrvStr3(reqData.getProsecutionWay());
        mainTrade.setRsrvStr5("SMS");
        mainTrade.setRsrvStr8(reqData.getSmsContent());
        mainTrade.setRemark("垃圾短信举报受理");
        // mainTrade.setProcessTagSet("0000000000000000000E10000000000000000000");
        mainTrade.setSerialNumber(reqData.getUca().getSerialNumber());
        mainTrade.setInModeCode(reqData.getInModeCode());
        mainTrade.setPriority("290");
        mainTrade.setAcctId(reqData.getUca().getAcctId());
    }

    private void geneTradeProduct(BusiTradeData btd, ProsecutionTradeReqData reqData) throws Exception
    {
        ProductTradeData productData = new ProductTradeData();
        productData.setUserId(reqData.getUca().getUserId());
        productData.setUserIdA("-1");
        productData.setProductId("0");
        productData.setProductMode("-1");
        productData.setBrandCode(reqData.getUca().getBrandCode());
        productData.setOldProductId(reqData.getUca().getProductId());
        productData.setOldBrandCode(reqData.getUca().getBrandCode());
        productData.setInstId(SeqMgr.getInstId());
        productData.setCampnId("-1");
        productData.setStartDate(SysDateMgr.getSysTime());
        productData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        productData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        productData.setRemark("垃圾短信举报受理");

        btd.add(reqData.getUca().getSerialNumber(), productData);
    }
}
