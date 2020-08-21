
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order.trade;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order.requestdata.FamilyFixPhoneReqData;

/**
 * 家庭固话
 */
public class FamilyFixPhoneTrade extends BaseTrade implements ITrade
{ 
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	String userid=btd.getRD().getUca().getUser().getUserId();
    	String custid=btd.getRD().getUca().getUser().getCustId();
    	String eparchycode=btd.getRD().getUca().getUser().getEparchyCode();
    	String citycode=btd.getRD().getUca().getUser().getCityCode();
    	
    	
    	String startDate=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
    	String endDate=SysDateMgr.getTheLastTime();
    	
    	FamilyFixPhoneReqData rd = (FamilyFixPhoneReqData) btd.getRD();
    	IData pageInfo=rd.getPageInfo();
    	String fixNum=pageInfo.getString("FIX_NUMBER","");
    	btd.getMainTradeData().setRsrvStr1(fixNum);
    	btd.getMainTradeData().setRsrvStr2("84003861");
    	
    	createDiscntTrade("-1","-1","84003861",startDate,endDate,btd);
	    createOtherTradeInfo(btd);  
    } 
    
    public void createDiscntTrade(String productId,String packageId,String discntId ,String startDate,String endDate,  BusiTradeData btd) throws Exception
	{
		 
		DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(btd.getRD().getUca().getUserId());
        newDiscnt.setUserIdA("-1");
        newDiscnt.setProductId(productId);
        newDiscnt.setPackageId(packageId);
        newDiscnt.setElementId(discntId);
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("0");
        newDiscnt.setStartDate(startDate);
        newDiscnt.setEndDate(endDate);
        newDiscnt.setRemark("家庭固话基本套餐");
        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);  
	}

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {
    	FamilyFixPhoneReqData rd = (FamilyFixPhoneReqData) btd.getRD();
    	IData pageInfo=rd.getPageInfo();
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
        
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("FIX_PHONE");
        otherTradeData.setRsrvValue("家庭固话");
        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(rd.getRemark()); 
        otherTradeData.setRsrvStr1(pageInfo.getString("FIX_NUMBER","")); 
        btd.add(serialNumber, otherTradeData); 
    }
}
