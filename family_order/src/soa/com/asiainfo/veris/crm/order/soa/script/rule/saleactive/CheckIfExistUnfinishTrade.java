package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * 办理宽带1+活动检查是否存在宽带未完工单。有则拦截
 */
public class CheckIfExistUnfinishTrade extends BreBase implements IBREScript{

private static final long serialVersionUID = 2987559940602315670L;
	
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		String serialNumber=databus.getString("SERIAL_NUMBER");
		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber); 
		//用户信息为空，说明是从开户那边进入
		if(IDataUtil.isEmpty(userInfo)){
			return false;
		} 
		
		
		String userId=userInfo.getString("USER_ID");
		String saleProductId = databus.getString("PRODUCT_ID");
		String packageId=databus.getString("PACKAGE_ID"); 
		String kd_serialNum="";
		String kd_userId="";
		if("69908001".equals(saleProductId)){
			if(!serialNumber.startsWith("KD_")){
				kd_serialNum="KD_"+serialNumber;
			}
			IData kdUserInfo=UcaInfoQry.qryUserInfoBySn(kd_serialNum); 
			if(IDataUtil.isEmpty(kdUserInfo)){
				return false;
			} 
			kd_userId=kdUserInfo.getString("USER_ID");
		}
		//查询拦截配置
		IDataset commRule=CommparaInfoQry.getCommNetInfo("CSM", "7717", saleProductId);
		for(int i=0;i<commRule.size();i++){
			String tradeTypeCode=commRule.getData(i).getString("PARA_CODE1","");
			IDataset noFinishTrade=TradeInfoQry.getMainTradeByUserIdTypeCode(kd_userId,tradeTypeCode,"");
			String errTag=commRule.getData(i).getString("PARA_CODE2","");
	        if("1".equals(errTag) && noFinishTrade!=null && noFinishTrade.size()>0){
	        	if("69908001".equals(saleProductId)){
		        	/**
		        	 * 查看是否存在包年套餐 
		        	 */
		        	IDataset yearActives=SaleActiveInfoQry.getUserSaleActiveInfoInUse(userId, "67220428");
		        	if(yearActives!=null && yearActives.size()>0){
		        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"7717001", "用户存在宽带移机未完工单，请等待完工后再办理该活动！");
		        		return true;
		        	} 
	        	}else{
	        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"7717001", "用户存在业务【"+tradeTypeCode+"】未完工单，请等待完工后再办理该活动！");
	        		return true;
	        	}
	        }		 
		} 
		return false; 
    } 
}
