package com.asiainfo.veris.crm.order.web.person.onecardmultino;




import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OneCardMultiNo extends PersonBasePage
{
	public abstract void setRelationList(IDataset relationList);

	/**
	 * 当前有效的营销活动列表查询供展示
	 * @param cycle
	 * @throws Exception
	 */
	public void qryOtherInfos(IRequestCycle cycle) throws Exception {
	
		 IData pgData = getData();
	        pgData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("CUST_EPARCHY_CODE"));
	        pgData.put("SERIAL_NUMBER", pgData.getString("AUTH_SERIAL_NUMBER"));
		 
	    IData param = new DataMap();
	    param.putAll(pgData);
	    param.put("SERIAL_NUMBER", pgData.getString("AUTH_SERIAL_NUMBER"));
	    	IDataset relationList = CSViewCall.call(this,"SS.OneCardMultiNoSVC.qryRelationListForCRM", param);
			if(IDataUtil.isEmpty(relationList)){
				CSViewException.apperr(CrmCommException.CRM_COMM_103,"该号码不是一卡多号用户！");
			}
			this.setRelationList(relationList);
	}
	/**
	 * 选中的营销活动的取消
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("CUST_EPARCHY_CODE"));
		
		//BUG20190329084458和多号副号取消提示“订购关系不存在”报错 by mengqx 20190330
		pageData.put("AUTH_SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER_A"));
		pageData.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER_A"));
		// end BUG20190329084458和多号副号取消提示“订购关系不存在”报错 by mengqx 20190330
		
		pageData.put("FLAG", "1");
		IDataset returnDataset = CSViewCall.call(this, "SS.OneCardMultiNoRegSVC.tradeReg", pageData);
        this.setAjax(returnDataset);
	}
	
	/**
	 * 营业厅：添加单个副号码
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmitApply(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("CUST_EPARCHY_CODE"));
		pageData.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		pageData.put("FLAG", "5");
		IDataset returnDataset = CSViewCall.call(this, "SS.OneCardMultiNoRegSVC.tradeReg", pageData);
        this.setAjax(returnDataset);
	}
	

	 public void onInitTrade(IRequestCycle cycle) throws Exception
	    {
	    }

}