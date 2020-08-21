
package com.asiainfo.veris.crm.order.soa.person.busi.terminalMarketConfiguer;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;


public class TerminalMarketConfiguerBean extends CSBizBean{
	
	 //查询方法
	 public IData qurTerminalData(IData param) throws Exception{
		 return Dao.qryByCodeOnlyOne("TF_F_TERMINAL_MARKET_CONFIG", "QRE_BY_ACTIVID", param);
	 }
	 //新增方法
	 public IData insertTerminalData(IData param) throws Exception{
		 
	 	IData data = new DataMap();
	 	IData dataTag = new DataMap();
	    data.put("ACTIV_ID", param.getString("ACTIV_ID"));
	    //查询终端营销活动表判断表中数据是否已经存在
	    IDataset dataNum = Dao.qryByCode("TF_F_TERMINAL_MARKET_CONFIG", "QRE_BY_ACTIVID", data);
	    if(DataUtils.isNotEmpty(dataNum)){
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_1189);
	    }else{
	    	Dao.insert("TF_F_TERMINAL_MARKET_CONFIG", param);
	    	dataTag.put("SUCCESS", "新增CMIOT终端营销活动配置成功");
	    }
	    return dataTag;
	 }
	 //修改方法
	 public IData updateTerminalData(IData param) throws Exception{
		 
		 	IData dataTag = new DataMap();
		 	Dao.executeUpdateByCodeCode("TF_F_TERMINAL_MARKET_CONFIG", "UPDATE_ANY", param);
	//    	Dao.update("TF_F_TERMINAL_MARKET_CONFIG", param,new String[]{"ACTIV_ID"});
	    	dataTag.put("SUCCESS", "修改CMIOT终端营销活动配置成功");
		    return dataTag;
		 }
	 //删除的方法
	 public void delTerminalData(IData params) throws Exception{
		 
		 IDataset datas = Dao.qryByCodeParser("TF_F_TERMINAL_MARKET_CONFIG", "QRE_BY_ACTIVID", params);
		 if(DataUtils.isNotEmpty(datas)){
			 IData data = datas.getData(0);
			 data.put("REMOVE_TAG", "1");
			 Dao.executeUpdateByCodeCode("TF_F_TERMINAL_MARKET_CONFIG", "UPDATE_BY_ACTIVID", data);
//			 Dao.update("TF_F_TERMINAL_MARKET_CONFIG", data); 
		 }else{
			 CSAppException.apperr(CrmCommException.CRM_COMM_1190);
		 }
		 
	 }
	
	 
	 
	 
}
