package com.asiainfo.veris.crm.order.soa.person.busi.gprslimit.order;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class GprsLockTrade extends BaseTrade implements ITrade{

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
        if (dataset != null && dataset.size() > 0){
        	OtherTradeData otherTradeDataDel = new OtherTradeData(dataset.getData(0));
	        otherTradeDataDel.setRsrvValue("1");
		    otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_UPD); 
	        otherTradeDataDel.setRemark("GPRS限速"); 
	        otherTradeDataDel.setRsrvStr1(SysDateMgr.getCurMonth());
	        otherTradeDataDel.setRsrvDate1(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        btd.add(serialNumber, otherTradeDataDel); 
        	
        }else {
	        OtherTradeData otherTradeDataDel = new OtherTradeData();
	        otherTradeDataDel.setUserId(userId);
	        otherTradeDataDel.setRsrvValueCode("GPRS_LIMIT");
	        otherTradeDataDel.setRsrvValue("1");
	        otherTradeDataDel.setInstId(SeqMgr.getInstId());
	        otherTradeDataDel.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());  
	        otherTradeDataDel.setEndDate(SysDateMgr.END_DATE_FOREVER);
		    otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_ADD); 	
	        otherTradeDataDel.setRemark("GPRS限速"); 
	        otherTradeDataDel.setRsrvStr1(SysDateMgr.getCurMonth());
	        otherTradeDataDel.setRsrvDate1(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        //otherTradeDataDel.setRsrvStr2("12898010000000000000000000000001");        
	        otherTradeDataDel.setRsrvStr3(serialNumber);
	        //otherTradeDataDel.setRsrvStr9("10203415");
	        btd.add(serialNumber, otherTradeDataDel); 
	    }
    }
}
