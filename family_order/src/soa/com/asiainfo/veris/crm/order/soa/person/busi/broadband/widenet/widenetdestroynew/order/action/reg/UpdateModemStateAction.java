package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
/**
 * 手机欠费销号，宽带关联销号时，光猫押金沉淀，状态修改为丢弃
 * @author zyc
 *
 */
public class UpdateModemStateAction implements ITradeAction {

	protected static Logger log = Logger.getLogger(UpdateModemStateAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String phoneDestroyType = btd.getRD().getPageRequestData().getString("PHONE_DESTROY_TYPE","");
		if(phoneDestroyType != null && "7240".equals(phoneDestroyType))
		{
			String phoneSerialNumber = "";
			
			String serialNumber = btd.getRD().getUca().getSerialNumber();
	        if(serialNumber.indexOf("KD_")>-1) {//宽带账号
	    		if(serialNumber.split("_")[1].length()>11)
	    			phoneSerialNumber = serialNumber ;
	    		else
	    			phoneSerialNumber = serialNumber.split("_")[1];//个人账号
	    	}
	    	else {
	    		if(serialNumber.length()>11)
	    			phoneSerialNumber = "KD_"+serialNumber ; //商务宽带
	    		else
	    			phoneSerialNumber = serialNumber ;
	    	}
			
			//查询号码是否正常
	        IData param = new DataMap();
	    	param.put("SERIAL_NUMBER", phoneSerialNumber);
	    	
	    	IDataset userinfo = CSAppCall.call("SS.DestroyUserNowSVC.getUserInfoBySerailNumber", param);
	    	if(IDataUtil.isNotEmpty(userinfo))
	    	{
	    		IDataset userOtherinfo = null;
	    		if(userinfo.getData(0).getString("RSRV_STR10","").equals("BNBD"))//商务宽带
	    		{
	    			userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryGroupUserOtherInfo", userinfo.first());
	    			if(IDataUtil.isNotEmpty(userOtherinfo))
	        		{
	    				String rsrvTag1 = userOtherinfo.getData(0).getString("RSRV_TAG1","");
	    				String rsrvTag2 = userOtherinfo.getData(0).getString("RSRV_TAG2","");
	    				if(rsrvTag1 != null && "0".equals(rsrvTag1))
	    				{
	    					if(rsrvTag2 != null && !"3".equals(rsrvTag2))
	    					{
	    						//只有租赁时才记录光猫的状态为丢失
		    					OtherTradeData data = new OtherTradeData(userOtherinfo.first());
								data.setModifyTag(BofConst.MODIFY_TAG_DEL);
								data.setRemark("欠费销号记录光猫丢失,押金沉淀");
								data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());

								data.setRsrvTag2("4");//记录光猫丢失状态

								btd.add(phoneSerialNumber, data);
	    					}
	    				}
	        		}
	    		}
	    		else
	    		{
	    			//userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
	    			userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryUserModemRent", userinfo.first());
	    			if(IDataUtil.isNotEmpty(userOtherinfo))
	        		{
	    				String rsrvTag1 = userOtherinfo.getData(0).getString("RSRV_TAG1","");
	    				String rsrvTag2 = userOtherinfo.getData(0).getString("RSRV_TAG2","");
	    				if(rsrvTag1 != null && "0".equals(rsrvTag1))
	    				{
	    					if(rsrvTag2 != null && !"3".equals(rsrvTag2))
	    					{
	    						String remark = "欠费销号记录光猫丢失,押金沉淀";
	    						//只有租赁时才记录光猫的状态为丢失
		    					OtherTradeData data = new OtherTradeData(userOtherinfo.first());
								data.setModifyTag(BofConst.MODIFY_TAG_DEL);
								data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());

								data.setRsrvTag2("4");//记录光猫丢失状态

								//押金沉淀
								String rsrvStr7 = userOtherinfo.getData(0).getString("RSRV_STR7","");
								String rsrvStr2 = userOtherinfo.getData(0).getString("RSRV_STR2","");
								if(rsrvStr7 != null && rsrvStr7.equals("0"))
								{
									String userId = userinfo.first().getString("USER_ID") ;
									IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
									if(IDataUtil.isNotEmpty(acctInfo))
									{
										IData inparams=new DataMap();
									    inparams.put("USER_ID", userId);
									    inparams.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
									    inparams.put("SERIAL_NUMBER", phoneSerialNumber); 
									    inparams.put("TRADE_FEE", rsrvStr2);
									    
									    IData inAcct = AcctCall.AMBackFee(inparams);
									    
									    if(IDataUtil.isNotEmpty(inAcct))
									    {
									    	String result = inAcct.getString("RESULT_CODE","");
									    	if("0".equals(result))
									    	{
									    		//修改押金状态已沉淀
									    		data.setRsrvStr7("3");//押金已沉淀
									    		remark += "成功";
									    	}
									    	else
									    	{
									    		log.error("欠费销号,光猫押沉淀,调用账务押金沉淀接口返回错误:错误代码:" + result + ";错误信息:" + inAcct.getString("RESULT_INFO","") );
									    		
									    		remark += "失败";
									    	}
									    }
									}
								}
								data.setRemark(remark);
								btd.add(phoneSerialNumber, data);
	    					}
	    				}
	        		}
	        			
	    		}
	    	}
		}

	}

}
