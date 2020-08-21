package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ExtTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.requestdata.WidenetProductRequestData;

public class WidenetChangeProductTrade extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade implements ITrade {
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception {
        WidenetProductRequestData widenetProductRD = (WidenetProductRequestData) btd.getRD();

        btd.getMainTradeData().setSubscribeType("300");

        btd.getMainTradeData().setRsrvStr5(widenetProductRD.getYearDiscntRemainFee());
        btd.getMainTradeData().setRsrvStr6(widenetProductRD.getRemainFee());
        btd.getMainTradeData().setRsrvStr7(widenetProductRD.getAcctReainFee());
        btd.getMainTradeData().setRsrvStr8(widenetProductRD.getWideActivePayFee());
        btd.getMainTradeData().setRsrvStr9(widenetProductRD.getReturnYearDiscntRemainFee());
        if (StringUtils.isNotBlank(widenetProductRD.getReturnYearDiscntRemainFee()) && widenetProductRD.getReturnYearDiscntRemainFee().compareTo("0") > 0) {
            btd.getMainTradeData().setRsrvStr10("JZF");
        }

    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception {
        super.createBusiTradeData(btd);
        appendTradeMainData(btd);
        // 中小企业快速办理集团成员新增入ext表
        createTradeExt(btd);

    }

    private void createTradeExt(BusiTradeData<BaseTradeData> btd) throws Exception {

        WidenetProductRequestData reqData = (WidenetProductRequestData) btd.getRD();

        if (StringUtils.isNotBlank(reqData.getEcSerialNumber()) && StringUtils.isNotBlank(reqData.getEcUserId())) {
            ExtTradeData newTeadeExt = new ExtTradeData();
            newTeadeExt.setAttrCode("ESOP");
            newTeadeExt.setAttrValue(reqData.getIbsysId());
            newTeadeExt.setRsrvStr1(reqData.getNodeId());
            newTeadeExt.setRsrvStr6(reqData.getRecordNum());
            newTeadeExt.setRsrvStr8(reqData.getBusiformId());
            newTeadeExt.setRsrvStr10("EOS");
            btd.add(reqData.getEcSerialNumber(), newTeadeExt);
        }
    }
}
