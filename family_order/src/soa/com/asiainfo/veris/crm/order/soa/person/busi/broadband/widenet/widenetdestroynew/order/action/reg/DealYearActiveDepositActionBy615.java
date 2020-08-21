
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * REQ201612080012_优化手机销户关联宽带销号的相关规则
 * <br/> 
 * 手机欠费销号，宽带关联销号时，对包年剩余费用做沉淀处理
 * <br/>
 * 由于 手机欠费销号  宽带修改为报停，则不对包年剩余费用做沉淀处理。
 * 只有当资源调SS.DestroyUserNowSVC.checkWideInfoAndDestroy做
 * 号码回收时，宽带做特殊拆机处理时才对包年剩余费用做沉淀处理。
 * @author zhuoyingzhi
 * @date 20180228
 */
public class DealYearActiveDepositActionBy615 implements ITradeAction
{
	public void executeAction(BusiTradeData btd) throws Exception 
    {
		String phoneDestroyType = btd.getRD().getPageRequestData().getString("PHONE_DESTROY_TYPE","");
		String tradeTypeCode = btd.getRD().getPageRequestData().getString("TRADE_TYPE_CODE","");
		
		System.out.println("---DealYearActiveDepositActionBy615-----phoneDestroyType:"+phoneDestroyType+",tradeTypeCode:"+tradeTypeCode);
		
		if(phoneDestroyType != null && "7240".equals(phoneDestroyType) && "615".equals(tradeTypeCode))
		{
			/********************只有到期处理时才执行***************************/
			
			//这里获取的是带KD的
	        String serialNumber = btd.getMainTradeData().getSerialNumber();
	        
	    	if(serialNumber.startsWith("KD_")){
	    		serialNumber = serialNumber.substring(3);
	    	}
	    	
	    	//查询已经销户的客户信息
	    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("4", serialNumber);
			System.out.println("---DealYearActiveDepositActionBy615-----userInfo:"+userInfo);
	    	if(IDataUtil.isEmpty(userInfo)){
	    		return ;
	    	}
	    	String seriUserId = userInfo.getData(0).getString("USER_ID");
	    	
	    	
	    	IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(seriUserId);
	    	System.out.println("---DealYearActiveDepositActionBy615-----acctInfo:"+acctInfo);
	    	if(IDataUtil.isEmpty(acctInfo)){
	    		return ;
	    	}
	    	
	    	//计算专项存折的费用 
	    	int balance9021 = 0;
	    	
	    	String acctId=acctInfo.getString("ACCT_ID");
	    	
	    	
	    	
	    	IDataset allUserMoney = AcctCall.queryAccountDepositByAcctId(acctId);
			System.out.println("---DealYearActiveDepositActionBy615-----allUserMoney:"+allUserMoney);
	    	if(IDataUtil.isEmpty(allUserMoney)){
	    		return;
	    	}
			
	    	
	    	for(int i=0;i<allUserMoney.size();i++){
	    		if("9021".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
	    			String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
	                int balance2 = Integer.parseInt(balance1);
	                balance9021 = balance9021 + balance2;
	    		}
	    	}
	    	
	    	System.out.println("---DealYearActiveDepositActionBy615-----balance9021:"+balance9021+",serialNumber:"+serialNumber);
	    	
	    	if(balance9021 <=0 ) return;
	    	
			//资金进行沉淀
			IData depositeParam=new DataMap();

			depositeParam.put("ACCT_ID", acctId);
			depositeParam.put("TRADE_FEE", balance9021);
			depositeParam.put("ANNUAL_TAG", "1");
			depositeParam.put("DEPOSIT_CODE", "9021");
			IData inAcct = AcctCall.AMBackFee(depositeParam);
			if(IDataUtil.isNotEmpty(inAcct)&&("0".equals(inAcct.getString("RESULT_CODE",""))||"0".equals(inAcct.getString("X_RESULTCODE","")))){
				// 成功！ 处理other表
			}else{
				CSAppException.appError("61312", "调用接口 AM_CRM_BackFee 接口错误:"+inAcct.getString("X_RESULTINFO"));
			}			
		}		
    }

}
