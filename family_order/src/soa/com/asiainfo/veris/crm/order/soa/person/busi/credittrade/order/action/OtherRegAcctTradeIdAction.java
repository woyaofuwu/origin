
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.action;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class OtherRegAcctTradeIdAction implements ITradeAction
{
	
    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String strXTransCode = btd.getRD().getXTransCode();
    	if("SS.CreditTradeRegSVC.tradeReg".equals(strXTransCode)){
    		
    		MainTradeData mainTradeData = btd.getMainTradeData();							//获取主台帐
    		String servReqId = btd.getRD().getPageRequestData().getString("TRADE_ID","");	//获取账务侧台帐流水号
    		mainTradeData.setServReqId(servReqId);											//记录账务侧台帐流水号
    		
    		/*UcaData uca = btd.getRD().getUca();
    		String strTradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
    		String strSerialNumber = btd.getMainTradeData().getSerialNumber();
    		OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setRsrvValueCode("ACCT_TRADE_ID");
            otherTradeData.setRsrvValue(servReqId);
            otherTradeData.setRsrvStr1(strXTransCode);
            otherTradeData.setRsrvStr2(strTradeTypeCode);
            otherTradeData.setRsrvStr3(strSerialNumber);
            otherTradeData.setRsrvStr30("RSRV_VALUE记录计费传入TRADE_ID，RSRV_STR1字段记录发起接口名称，RSRV_STR2字段记录业务类型，RSRV_STR3字段记录手机号码。");
            otherTradeData.setUserId(uca.getUserId());
            otherTradeData.setStartDate(btd.getRD().getAcceptTime());
            otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值
            otherTradeData.setRemark("发起接口名称SS.CreditTradeRegSVC.tradeReg，该表字段RSRV_VALUE记录计费传入TRADE_ID");
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(uca.getSerialNumber(), otherTradeData);*/
    	}
    }

}
