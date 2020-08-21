
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEAreaLockReqData;

/**
 * CPE小区锁定
 * @author chenxy3 2015-8-25
 */
public class CPEAreaLockTrade extends BaseTrade implements ITrade
{
	
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
    	IDataset dataset = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "CPE_LOCATION", null);// 字段不全
    	IDataset locationdata = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "CPE_LOCATION", null);
    	if (dataset != null && dataset.size() > 0)
	    {
    		if (locationdata != null && locationdata.size() > 0)
    	    {
    			//多次锁定
    			// 用查询出来的tf_f_user_other对象来构建otherTradeData
    	        // 注意： 一定要保证查询出来数据包含了tf_f_user_other表的所有列，否则在完工时会将没有的列值update为空了
    	        OtherTradeData otherTradeDataDel = new OtherTradeData(dataset.getData(0));
    	        CPEAreaLockReqData rd = (CPEAreaLockReqData) btd.getRD();
    	        
    	        otherTradeDataDel.setRsrvValue("0");//锁定 
    	        otherTradeDataDel.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());  
    	        otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_UPD); 
    	        otherTradeDataDel.setRsrvStr22(rd.getCellName());  //REQ201602160009 CPE无线宽带锁定小区的数量改成6个
    	        otherTradeDataDel.setRsrvStr21(rd.getCellId());  //REQ201602160009 CPE无线宽带锁定小区的数量改成6个             
    	        otherTradeDataDel.setRsrvStr2("12898010000000000000000000000001");        
    	        otherTradeDataDel.setRsrvStr3(serialNumber);
    	        otherTradeDataDel.setRsrvStr9("10203415");
    	        otherTradeDataDel.setRemark(rd.getRemark());
    	        btd.add(serialNumber, otherTradeDataDel); 
    	    }else{
 	    	   //第一次锁定
 	    		OtherTradeData otherTradeDataDel = new OtherTradeData(dataset.getData(0));
 		        CPEAreaLockReqData rd = (CPEAreaLockReqData) btd.getRD();
 		        otherTradeDataDel.setRsrvValueCode("CPE_LOCATION");
 		        otherTradeDataDel.setRsrvValue("0");//锁定
 		        otherTradeDataDel.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());

 		        otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_ADD); 
 		        otherTradeDataDel.setRemark(rd.getRemark());     
 		        otherTradeDataDel.setRsrvStr22(rd.getCellName());  //REQ201602160009 CPE无线宽带锁定小区的数量改成6个
 		        otherTradeDataDel.setRsrvStr21(rd.getCellId());  //REQ201602160009 CPE无线宽带锁定小区的数量改成6个             
 		        otherTradeDataDel.setRsrvStr2("12898010000000000000000000000001");        
 		        otherTradeDataDel.setRsrvStr3(serialNumber);
 		        otherTradeDataDel.setRsrvStr9("10203415");
 		        otherTradeDataDel.setRemark(rd.getRemark());
 		        otherTradeDataDel.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
 		        btd.add(serialNumber, otherTradeDataDel);        
 	    }
	        
	    }else{
	    	String x_resultinfo="该CPE用户【"+serialNumber+"】未绑定付费关系。";
	    	CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
//	    	CPEAreaLockReqData rd = (CPEAreaLockReqData) btd.getRD();
//	        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
//	        String cellId=rd.getCellId();
//	        OtherTradeData otherTradeData = new OtherTradeData();
//	        otherTradeData.setRsrvValueCode("CPE_LOCATION");
//	        otherTradeData.setRsrvValue("0");//锁定
//	        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
//	        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
//	        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
//	        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
//	        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
//	        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
//	        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
//	        otherTradeData.setRemark(rd.getRemark()); 
//	        otherTradeData.setRsrvStr1(rd.getCellId());
//	        otherTradeData.setRsrvStr2("12898010000000000000000000000001");        
//	        otherTradeData.setRsrvStr3(serialNumber);
//	        otherTradeData.setRsrvStr9("10203415");
//	        btd.add(serialNumber, otherTradeData); 
	    }
    }
}
