package com.asiainfo.veris.crm.order.soa.person.busi.gprslimit.order;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class GprsUnLockTrade extends BaseTrade implements ITrade{
	
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	// 成功！ 处理other表
	    createOtherTradeInfo(btd); 
    } 

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {   
    	String userId=btd.getRD().getUca().getUserId();
    	String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();  
    	IDataset dataset = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "GPRS_LIMIT", null);// 字段不全
    	OtherTradeData otherTradeDataDel = new OtherTradeData(dataset.getData(0));
	        otherTradeDataDel.setRsrvValue("0");
		    otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_UPD); 
	        otherTradeDataDel.setRemark("GPRS解除限速"); 
	        //otherTradeDataDel.setRsrvStr1(SysDateMgr.getCurMonth());       
	        otherTradeDataDel.setRsrvDate2(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        otherTradeDataDel.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        btd.add(serialNumber, otherTradeDataDel); 
	        
    }

}
