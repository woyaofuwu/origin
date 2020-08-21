package com.asiainfo.veris.crm.order.soa.person.busi.mobileSpecialepay.order.trade;


import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.mobileSpecialepay.order.requestdata.MobileSpecialepayReqData;

public class MobileSpecialepayTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        MainTradeData mtd = bd.getMainTradeData();
        mtd.setRemark("修改移动员工代付费");
    	
    	createTradeOther(bd);
    }

    public void createTradeOther(BusiTradeData bd) throws Exception
    {
    	OtherTradeData otherTD = new OtherTradeData();
    	
    	MobileSpecialepayReqData reqData = (MobileSpecialepayReqData) bd.getRD();
        String userid = reqData.getUca().getUserId();
        
        IDataset results = UserOtherInfoQry.getUserOther(userid, "30");
        IDataset resultset = UserOtherInfoQry.getUserOther(userid, "40");
        
        if(IDataUtil.isNotEmpty(results)&&IDataUtil.isNotEmpty(resultset)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户有移动员工代付费和非移动员工代付费，无法办理员工代付费调整!");
        }else{
        	
        	otherTD.setUserId(userid);
    		otherTD.setRsrvValue(reqData.getPAY_MONEY());
    		otherTD.setStartDate(SysDateMgr.getSysDate());
    		otherTD.setEndDate(SysDateMgr.getTheLastTime());
    		otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
    		otherTD.setInstId(SeqMgr.getInstId());
    		
    		
        	
        	if(IDataUtil.isEmpty(results)&&IDataUtil.isEmpty(resultset)){
        		otherTD.setRsrvValueCode("30");
        		otherTD.setRemark("新增移动员工代付费");
        		
        	}else if(IDataUtil.isEmpty(results)&&IDataUtil.isNotEmpty(resultset)){
        		
        		otherTD.setRsrvValueCode("40");
        		otherTD.setRemark("修改非移动员工代付费");
        		
        		OtherTradeData otherTDdelold = new OtherTradeData(resultset.getData(0));//终止旧数据
        		otherTDdelold.setEndDate(SysDateMgr.getSysDate());
        		otherTDdelold.setModifyTag(BofConst.MODIFY_TAG_UPD);
        		bd.add(reqData.getUca().getSerialNumber(), otherTDdelold);
        		
        	}else if(IDataUtil.isNotEmpty(results)&&IDataUtil.isEmpty(resultset)){
        		otherTD.setRsrvValueCode("30");
        		otherTD.setRemark("修改移动员工代付费");
        		
        		OtherTradeData otherTDdelold = new OtherTradeData(results.getData(0));//终止旧数据
        		otherTDdelold.setEndDate(SysDateMgr.getSysDate());
        		otherTDdelold.setModifyTag(BofConst.MODIFY_TAG_UPD);
        		bd.add(reqData.getUca().getSerialNumber(), otherTDdelold);
        	}
        	
        	bd.add(reqData.getUca().getSerialNumber(), otherTD);
        	
        }
    }
    

}
