
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.requestdata.ApnUserBindingForOlcomReqData;

public class ApnUserBindingForOlcomTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
    	ApnUserBindingForOlcomReqData apnUserReq = (ApnUserBindingForOlcomReqData) bd.getRD();
        
        String apnName = apnUserReq.getApnName();
        String apnDesc = apnUserReq.getApnDesc();
        String apnCntxId = apnUserReq.getApnCntxId();
        String apnTplId = apnUserReq.getApnTplId();
        String apnType = apnUserReq.getApnType();
        String apnIP4Add = apnUserReq.getApnIPV4Add();
        
    	MainTradeData mainTrade = bd.getMainTradeData();
    	String strUserId = mainTrade.getUserId();
    	String strSerialNumber = mainTrade.getSerialNumber();
    	
    	IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValue(strUserId, "USER_APNTAG", strSerialNumber);
    	if(IDataUtil.isEmpty(otherInfos))
    	{
    		this.createOtherTradeInfo(bd);
    	}
    	
    	mainTrade.setRsrvStr1(apnName);
    	mainTrade.setRsrvStr2(apnDesc);
    	mainTrade.setRsrvStr3(apnCntxId);
    	mainTrade.setRsrvStr4(apnTplId);
    	mainTrade.setRsrvStr5(apnType);
    	mainTrade.setRsrvStr6(apnIP4Add);
        
    }

    /**
     * 
     * @param bd
     * @param apnUserReq
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData bd) throws Exception
    {
    	OtherTradeData otherTradeData = new OtherTradeData();
    	
    	MainTradeData mainTrade = bd.getMainTradeData();
    	String strUserId = mainTrade.getUserId();
    	String strSerialNumber = mainTrade.getSerialNumber();
    	    	
    	otherTradeData.setRsrvValueCode("USER_APNTAG");
    	otherTradeData.setRsrvValue(strSerialNumber);
        otherTradeData.setUserId(strUserId);
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark("用户专用APN绑定IP新增(发指令)");
        bd.add(strSerialNumber, otherTradeData);
    }

}
