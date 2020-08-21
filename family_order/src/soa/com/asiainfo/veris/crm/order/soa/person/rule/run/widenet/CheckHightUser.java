package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 校验是否符合高价值小区用户
 *  查询提示
 * @author chenzm
 * @date 2014-05-23
 */
public class CheckHightUser extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		 String serialNumber = databus.getString("SERIAL_NUMBER").replace("KD_", "");
		 System.out.println("定位checkHightUser："+serialNumber);
		 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
     	 if(IDataUtil.isNotEmpty(userInfo)){
     		 String userId = userInfo.getString("USER_ID");
     		 IDataset commparaInfos9923 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9923",null,null);
	   		 if(IDataUtil.isNotEmpty(commparaInfos9923)){
	   			 String productId = commparaInfos9923.getData(0).getString("PARA_CODE1");
	   			 IData params=new DataMap();
	   			 params.put("USER_ID", userId);
	   			 params.put("PRODUCT_ID", productId);
	   			 IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);  
	   			 if(IDataUtil.isNotEmpty(iDataset)){
	   				String startDate = iDataset.getData(0).getString("START_DATE");
	   				String endDate = iDataset.getData(0).getString("END_DATE");
	   				String errorInfo = "尊敬的用户，您参加的高价值小区专项营销活动尚未到期（"+startDate+"--"+endDate+"），无法进行拆机。";
	   	            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604033", errorInfo);
	   			 }
	   		 }
     	 }
		 
		 
     
		
	        
	       
    
		return false;
	}

}
