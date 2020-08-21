
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.bank.order.requestdata.BankBindDealRequestData;

/**
 * 手机和银联卡绑定台账登记
 * 
 * @author wukw3
 */
public class BankBindDealRegTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	// 更新主台账的费用信息
        dealMainTradeData(btd);
        createTradeOther(btd);
    }
    
    public void dealMainTradeData(BusiTradeData btd) throws Exception
    {
    	BankBindDealRequestData reqData = (BankBindDealRequestData) btd.getRD();
        // 更新主台账的费用信息
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1(reqData.getBankCardNo());
        mainTradeData.setRsrvStr2(reqData.getBankName());
        mainTradeData.setRsrvStr3(reqData.getPassWord());

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
    	BankBindDealRequestData reqData = (BankBindDealRequestData) btd.getRD();
        OtherTradeData otherTD = new OtherTradeData();

        otherTD.setUserId(reqData.getUca().getUser().getUserId());
        otherTD.setRsrvValueCode("BANKBIND");
        otherTD.setRsrvValue("手机号码与银联卡绑定");
        otherTD.setRsrvStr1(reqData.getBankCardNo());
        otherTD.setRsrvStr2(reqData.getBankName());
        otherTD.setRsrvStr3(reqData.getPassWord());

        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setStartDate(reqData.getAcceptTime());
        otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        otherTD.setInstId(SeqMgr.getInstId());

        btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
    }

}
