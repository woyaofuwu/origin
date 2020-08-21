package com.asiainfo.veris.crm.iorder.web.person.giveuserclass;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GiveUserClass extends PersonBasePage {
	private final static Logger logger = Logger.getLogger(GiveUserClass.class);

	/**
	 * 添加新手机号码时校验
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void checkAddMeb(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
	    logger.error("GiveUserClassxxxxxxxxxxxxxxxxxxxxxxxx26 "+pageData);							 											
	    
		IDataset ds = new DatasetList();
		String snBList = pageData.getString("SERIAL_NUMBER_B_LIST", "");//副号
		String mianSn = pageData.getString("SERIAL_NUMBER", "");//主号
		String[] serialNumberBList = snBList.split(",");

		IData paramData = new DataMap();
		paramData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		IData returnData = new DataMap();
		StringBuilder errorList = new StringBuilder();
		IDataset successList = new DatasetList();
		IData success = null;
		StringBuilder errorNumList = new StringBuilder();
		for (int i = 0; i < serialNumberBList.length; i++) {
			success = new DataMap();
			String serialNumberB = serialNumberBList[i];
			paramData.put("SERIAL_NUMBER_B", serialNumberB);
			paramData.put("SERIAL_NUMBER", mianSn);
			paramData.put("USER_CLASS", pageData.getString("USER_CLASS"));
			ds = CSViewCall.call(this, "SS.GiveUserClassSVC.checkAddMeb", paramData);
			if ("-1".equals(ds.getData(0).getString("X_RESULTCODE"))) {
				String errorInfo = ds.getData(0).getString("X_RESULTINFO");
				//errorList.append(serialNumberB);
				//errorList.append(":");
				errorList.append(errorInfo);
				//errorList.append("; ");

				errorNumList.append(serialNumberB);
				errorNumList.append(" ");
			} else {
				success.put("SERIAL_NUMBER_B", serialNumberB);
				success.put("START_DATE", ds.getData(0).getString("START_DATE"));
				success.put("END_DATE", ds.getData(0).getString("END_DATE"));
				successList.add(success);
			}
		}

		returnData.put("errorList", errorList.toString());
		returnData.put("errorNumList", errorNumList.toString());
		returnData.put("successList", successList);
		setAjax(returnData);
	}

	public void loadInfo(IRequestCycle cycle) throws Exception {
		IData data = getData();
		IData cond = new DataMap();
		IData otherdata = new DataMap();
		cond.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
		IDataset giveUserClassinfos = CSViewCall.call(this, "SS.GiveUserClassSVC.queryUserClassBySn", cond);
		if(IDataUtil.isNotEmpty(giveUserClassinfos)){
			IData giveuserclassinfo = giveUserClassinfos.getData(0);
			/*int total_num = ;
			String userclassname = ;*/
			otherdata.put("AddMebMaxNum", giveuserclassinfo.getInt("TOTAL_NUM"));
			otherdata.put("USER_CLASS_NAME", giveuserclassinfo.getString("USER_CLASS_NAME"));
			otherdata.put("USER_CLASS", giveuserclassinfo.getString("USER_CLASS"));


		}
		
		IDataset giveUserClassDetailinfos = CSViewCall.call(this, "SS.GiveUserClassSVC.queryUserClassDetailBySn", cond);
		
		setViceInfos(giveUserClassDetailinfos);
		setUsergiveclassInfos(giveUserClassinfos);
		setOtherInfo(otherdata);		
		
	}
	
	public void clearpagedata(IRequestCycle cycle) throws Exception {	 
		setViceInfos(new DatasetList());
		setUsergiveclassInfos(new DatasetList());
		setOtherInfo(new DataMap());				
	}
	
	public void submitData(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		pageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
	    logger.error("GiveUserClassxxxxxxxxxxxxxxxxxxxxxxxx109 "+pageData);							 											

		IDataset rtDataset = CSViewCall.call(this, "SS.GiveUserClassSVC.submitData", pageData);
		//this.setAjax(rtDataset);
		setAjax(rtDataset.getData(0));
		
	}
	
	public abstract void setUsergiveclassInfo(IData discntInfo);
	
	public abstract void setUsergiveclassInfos(IDataset discntInfos);
	
	public abstract void setViceInfo(IData viceInfo);
	
	public abstract void setViceInfos(IDataset viceInfos);
	
	public abstract void setOtherInfo(IData otherInfo);

}
