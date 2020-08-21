package com.asiainfo.veris.crm.order.soa.person.common.action.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 移动商城2.8-入参报文新增全网统一渠道编码字段，省份需要保存，用于全网渠道统计分析
 * @author 
 * @date 
 */
public class OrderDifferentiationChannelAction implements ITradeAction{
	
	private static transient final Logger logger = Logger.getLogger(OrderDifferentiationChannelAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
        String transCode = btd.getRD().getXTransCode();//省BOSS能开接口编码
        if("".equals(transCode) || null==transCode){
        	return;
        }
        String channleId = btd.getRD().getPageRequestData().getString("UNI_CHANNEL");//19位渠道编码
        String netexpensesCode = btd.getRD().getPageRequestData().getString("NET_EXPENSES_CODE");//全网资费编码
        String oprnumb = btd.getRD().getPageRequestData().getString("OPRNUMB");//流水号

        if("".equals(channleId) || null==channleId){
        	return;
        }
        IDataset PBossCOPIConfig = CommparaInfoQry.getCommparaByCodeCode1("CSM", "617", "PBOSS_COPI", transCode);
        if (IDataUtil.isNotEmpty(PBossCOPIConfig)){
    		OtherTradeData otherTrade = new OtherTradeData();
            otherTrade.setUserId(btd.getRD().getUca().getUserId());
            otherTrade.setRsrvValueCode("CHANNLEIDINFO");
            otherTrade.setRsrvValue("渠道编码信息");
            otherTrade.setStartDate(btd.getRD().getAcceptTime());
            otherTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTrade.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTrade.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTrade.setRsrvStr1(channleId);
            otherTrade.setRsrvStr2(btd.getTradeId());//trade_id
            otherTrade.setRsrvStr3(netexpensesCode);//全网资费编码
            otherTrade.setRsrvStr4(oprnumb);//流水号
            otherTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTrade.setInstId(SeqMgr.getInstId());
            btd.add(btd.getRD().getUca().getSerialNumber(), otherTrade);
        }
	}

}
