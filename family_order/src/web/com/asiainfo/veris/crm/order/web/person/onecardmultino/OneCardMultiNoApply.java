package com.asiainfo.veris.crm.order.web.person.onecardmultino;

import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OneCardMultiNoApply extends PersonBasePage
{
	public static Logger logger = Logger.getLogger(OneCardMultiNoApply.class);
	public abstract void setServiceList(IDataset serviceList);
	public abstract void setServiceInfos(IDataset serviceInfos);
	private IDataset viceNums=null;
	/**Description	Resource	Path	Location	Type
	 * 调用资源获得一个副号码
	 * @param cycle
	 * @throws Exception
	 */
	public void querySubSerialNumber (IRequestCycle cycle) throws Exception {
		IData input = getData();
		input.put("SERIAL_NUMBER", input.getString("AUTH_SERIAL_NUMBER"));
		input.put("EPARCHY_CODE", getTradeEparchyCode());
		viceNums =  CSViewCall.call(this,"SS.OneCardMultiNoSVC.qrySubSerialNumber", input);
		if(IDataUtil.isEmpty(viceNums)){
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到可订购的虚拟副号码！");
		}
		setAjax(viceNums); 
	} 

	
	public void qryServiceList(IRequestCycle cycle) throws Exception {
		
		 IData pgData = getData();
	        pgData.put(Route.ROUTE_EPARCHY_CODE, pgData.getString( "CUST_EPARCHY_CODE"));
	        pgData.put("SERIAL_NUMBER", pgData.getString("AUTH_SERIAL_NUMBER"));
		 
	    IData param = new DataMap();
	    param.putAll(pgData);
	    param.put("SERIAL_NUMBER", pgData.getString("AUTH_SERIAL_NUMBER"));
	    	IDataset servicelist = CSViewCall.call(this,"SS.OneCardMultiNoSVC.qryserviceList", param);
			if(IDataUtil.isEmpty(servicelist)){
				CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到可订购的服务！");
			}
			this.setServiceList(servicelist);
	}
	
	public void init(IRequestCycle cycle) throws Exception{
		IDataset dataset = UpcViewCall.querySpServiceBySpCodeAndBizCodeAndBizStateCodeRsrvStr1(this, "698034", "");
//		DataHelper.sort(dataset,"OFFER_NAME", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
		this.setServiceInfos(dataset);
	}
	
	/**
	 * 营业厅：添加单个副号码
	 * @param cycle
	 * @throws Exception 

	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IData selectedNums = new DataMap();
		pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("CUST_EPARCHY_CODE"));
		pageData.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		pageData.put("SERVICE_ID", pageData.getString("OFFER_CODE"));
		String selectViceNum = pageData.getString("SERIAL_NUMBER_B");
		String followMsisdns = pageData.getString("FOLLOW_MSISDN_S","");
		if(StringUtils.isNotBlank(followMsisdns)) {
			IDataset viceNums = new DatasetList(followMsisdns);
			IDataset followMsisdnStr = new DatasetList();
			if (DataUtils.isNotEmpty(viceNums)) {
				for (int i = 0; i < viceNums.size(); i++) {
					if (!viceNums.getData(i).getString("ACCESS_NUMBER").equals(selectViceNum)) {
						followMsisdnStr.add(viceNums.getData(i).getString("ACCESS_NUMBER"));
					}
				}
				selectedNums.put("VIRTUAL_ARR", followMsisdnStr);
				CSViewCall.call(this, "SS.OneCardMultiNoSVC.releaseVirtualUseNum", selectedNums);
			}
		}
		pageData.put("OPR_CODE", "06");
		IDataset returnDataset = CSViewCall.call(this, "SS.OneCardMultiNoApply.tradeReg", pageData);
		
        this.setAjax(returnDataset);
	}
	/**
	 * 释放未选择号码
	 * @param cycle
	 * @throws Exception 
	 */
	public void releaseUnuseNumber(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IData selectedNums = new DataMap();
		String followMsisdns = pageData.getString("FOLLOW_MSISDN_S","");
		String selectViceNum = pageData.getString("SERIAL_NUMBER_B");
		if(StringUtils.isNotBlank(followMsisdns)) {
			IDataset viceNums = new DatasetList(followMsisdns);
			IDataset followMsisdnStr = new DatasetList();
			if(StringUtils.isNotBlank(selectViceNum)){
				for (int i = 0; i < viceNums.size(); i++) {
					if (!viceNums.getData(i).getString("ACCESS_NUMBER").equals(selectViceNum)) {
						followMsisdnStr.add(viceNums.getData(i).getString("ACCESS_NUMBER"));
					}
				}
			}else{
				for (int i = 0; i < viceNums.size(); i++) {
					followMsisdnStr.add(viceNums.getData(i).getString("ACCESS_NUMBER"));
				}
			}
			selectedNums.put("VIRTUAL_ARR", followMsisdnStr);
			CSViewCall.call(this, "SS.OneCardMultiNoSVC.releaseVirtualUseNum", selectedNums);
		}
	}
}