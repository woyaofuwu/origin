package com.asiainfo.veris.crm.order.soa.person.busi.m2mtag.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.m2mtag.trade.requestdata.M2MTagReqData;

	public class M2MTagTrade extends BaseTrade implements ITrade
	{
	    public void createBusiTradeData(BusiTradeData btd) throws Exception
	    { 
	    	M2MTagReqData rd = (M2MTagReqData) btd.getRD();
		    createOtherTradeInfo(btd,rd); 
		    
	    } 

	    /**
	     * 处理other台账表
	     * 
	     * @param btd
	     * @throws Exception
	     */
	    private void createOtherTradeInfo(BusiTradeData btd,M2MTagReqData rd) throws Exception
	    {
	        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

	        
	        OtherTradeData otherTradeData = new OtherTradeData();
	        otherTradeData.setRsrvValueCode("HYYYKBATCHOPEN");// 材料编码
	        otherTradeData.setRsrvValue("行业应用卡批量开户标记");
	        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
	        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
	        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
	        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
	        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
	        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
	        otherTradeData.setRemark(rd.getRemark()); //备注
	        otherTradeData.setRsrvStr1(serialNumber);//
	        otherTradeData.setRsrvStr2(rd.getUca().getCustId());//
	        otherTradeData.setRsrvStr3("m2mTag");//
	        otherTradeData.setRsrvStr4("10");//
	        otherTradeData.setRsrvStr5(CSBizBean.getVisit().getStaffName());//
	        otherTradeData.setRsrvStr11(CSBizBean.getVisit().getStaffId());//
	       
	        btd.add(serialNumber, otherTradeData);
	        
	    }
}
