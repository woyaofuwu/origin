
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.ApnUserBindingForOlcomQry;
import com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.requestdata.ApnUserBindingForOlcomReqData;

public class ApnUserUnBindingForOlcomTrade extends BaseTrade implements ITrade
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
        String instId = apnUserReq.getApnInstId();
        
    	MainTradeData mainTrade = bd.getMainTradeData();
    	String strUserId = mainTrade.getUserId();
    	String strSerialNumber = mainTrade.getSerialNumber();
    	
    	boolean otherFlag = false;
    	IData inParam = new DataMap();
    	inParam.put("USER_ID", strUserId);
		inParam.put("REMOVE_TAG", "0");
    	IDataset apnInfos = ApnUserBindingForOlcomQry.queryUserApnInfoByUserId(inParam);
    	if(IDataUtil.isNotEmpty(apnInfos) && apnInfos.size() <= 1)
    	{
    		otherFlag = true;
    	} 
    	else if(IDataUtil.isEmpty(apnInfos) && apnInfos.size() == 0)
    	{
    		otherFlag = true;
    	}
    	
    	if(otherFlag)
    	{
    		IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValue(strUserId, "USER_APNTAG", strSerialNumber);
        	if(IDataUtil.isNotEmpty(otherInfos))
        	{
        		IData otherInfo = otherInfos.getData(0);
        		this.createOtherTradeInfo(bd,otherInfo);
        	}
    	}
    	
    	mainTrade.setRsrvStr1(apnName);
    	mainTrade.setRsrvStr2(apnDesc);
    	mainTrade.setRsrvStr3(apnCntxId);
    	mainTrade.setRsrvStr4(apnTplId);
    	mainTrade.setRsrvStr5(apnType);
    	mainTrade.setRsrvStr6(apnIP4Add);
    	mainTrade.setRsrvStr7(instId);
    }

    /**
     * 处理other表数据
     * @param bd
     * @param apnUserReq
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData bd,IData otherInfo) throws Exception
    {
    	OtherTradeData otherTradeData = new OtherTradeData(otherInfo);
    	
    	MainTradeData mainTrade = bd.getMainTradeData();
    	String strSerialNumber = mainTrade.getSerialNumber();
    	
        otherTradeData.setEndDate(SysDateMgr.getSysTime());
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD); 
        otherTradeData.setRemark("用户专用APN绑定IP删除(发指令)");
        bd.add(strSerialNumber, otherTradeData);
    }

}
