
package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.requestdata.RealNameRegReqData;

/**
 * 手机实名制预登记台账登记
 * 
 * @author liutt
 */
public class RealNameRegTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createTradeOther(btd);
    }

    /**
     * 处理台帐Other子表的数据
     * 
     * @param reqData
     * @param btd
     * @throws Exception
     */
    private void createTradeOther(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        // createTradeDatumOther(btd)//TODO 可选材料登记台账
        RealNameRegReqData reqData = (RealNameRegReqData) btd.getRD();
        OtherTradeData otherTD = new OtherTradeData();

        otherTD.setUserId(reqData.getUca().getUser().getUserId());
        otherTD.setRsrvValueCode("REAL");
        otherTD.setRsrvValue("实名制预受理");
        // RSRV_STR1，缺省不属于集团客户
        otherTD.setRsrvStr1("0");
        otherTD.setRsrvStr2(reqData.getCustName());
        otherTD.setRsrvStr3(reqData.getPsptTypeCode());
        otherTD.setRsrvStr4(reqData.getPsptId());
        otherTD.setRsrvStr5(reqData.getPsptAddr());
        otherTD.setRsrvStr6(reqData.getPhone());

        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setStartDate(reqData.getAcceptTime());
        otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        otherTD.setInstId(SeqMgr.getInstId());

        btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
    }

}
