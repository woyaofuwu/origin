package com.asiainfo.veris.crm.order.web.person.writeModemCode;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WriteModemCode extends PersonBasePage
{
	public abstract void setInfo(IData info);

	public abstract void setCondition(IData condition);

	/**
	 * 载入
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void initPage(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		String custName =  pageData.getString("CUST_NAME","");
		custName=new String(custName.getBytes("ISO-8859-1"),"GBK");

		IData data = new DataMap();
		data.put("IBSYSID", pageData.getString("IBSYSID",""));
		data.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER",""));
		data.put("ACCEPT_MONTH", pageData.getString("ACCEPT_MONTH",""));
		data.put("TRADE_ID", pageData.getString("SUBSCRIBE_ID",""));
		data.put("USER_ID", pageData.getString("USER_ID",""));
		data.put("CUST_NAME", custName);
		setInfo(data);
		setCondition(data);
	}
	public void checkModem(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		IData param = new DataMap();
		IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkModem", data);
		IData retData = dataset.first();
		data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		data.put("RES_NO", data.getString("RES_ID"));
		data.put("USER_ID", data.getString("USER_ID"));
		if(data.getString("SERIAL_NUMBER").length() == 11)
		{
			IDataset crmdataSet = CSViewCall.call(this, "SS.FTTHModermApplySVC.updModermNumber", data);
			IDataset assureUserDataset = null;
			IDataset assureCustDataset = null;
			if (DataSetUtils.isNotBlank(crmdataSet) && StringUtils.equals(crmdataSet.first().getString("X_RESULTCODE"), "0")){
				param.put("TRADE_ID", data.getString("TRADE_ID"));
				param.put("RES_NO", data.getString("RES_ID"));//串号 
				param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
				param.put("DEVICE_COST", dataset.first().getString("DEVICE_COST"));
				param.put("BILL_ID", data.getString("SERIAL_NUMBER"));
				IData param4UserQry = new DataMap();
				param4UserQry.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
				assureUserDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoBySn", param4UserQry);
				if (assureUserDataset.isEmpty())
				{
					CSViewException.apperr(CustException.CRM_CUST_134, data.getString("SERIAL_NUMBER"));
				}
				IData param4CustQry = new DataMap();
				param4CustQry.put("CUST_ID", assureUserDataset.get(0, "CUST_ID"));
				param4CustQry.put(Route.ROUTE_EPARCHY_CODE, assureUserDataset.get(0, "EPARCHY_CODE"));
				assureCustDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryCustInfoByCustId", param4CustQry);
				if (assureCustDataset.isEmpty())
				{
					CSViewException.apperr(CustException.CRM_CUST_134, data.getString("SERIAL_NUMBER"));
				}
				String custName =  String.valueOf(assureCustDataset.get(0, "CUST_NAME"));
				param.put("CUST_NAME",  custName);
				IDataset resdata = CSViewCall.call(this, "SS.TopSetBoxSVC.updateModem", param);
			}
			else{
				String resultInfo = crmdataSet.first().getString("X_RESULTINFO", "crm调用异常！[光猫]");
				CSViewException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
			}
		}
		else
		{
			IDataset crmdataSet = CSViewCall.call(this, "SS.FTTHBusiModemApplySVC.updFtthBusiResNO", data);
			IDataset assureUserDataset = null;
			IDataset assureCustDataset = null;
			if (DataSetUtils.isNotBlank(crmdataSet) && StringUtils.equals(crmdataSet.first().getString("X_RESULTCODE"), "0")){
				param.put("TRADE_ID", data.getString("TRADE_ID"));
				param.put("RES_NO", data.getString("RES_ID"));//串号 
				param.put("SERIAL_NUMBER", "KD_" + data.getString("SERIAL_NUMBER"));
				param.put("DEVICE_COST", dataset.first().getString("DEVICE_COST"));
				param.put("BILL_ID", "KD_" + data.getString("SERIAL_NUMBER"));
				IData param4UserQry = new DataMap();
				param4UserQry.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER").substring(0, 11));
				assureUserDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserInfoBySn", param4UserQry);
				if (assureUserDataset.isEmpty())
				{
					CSViewException.apperr(CustException.CRM_CUST_134, data.getString("SERIAL_NUMBER"));
				}
				IData param4CustQry = new DataMap();
				param4CustQry.put("CUST_ID", assureUserDataset.get(0, "CUST_ID"));
				param4CustQry.put(Route.ROUTE_EPARCHY_CODE, assureUserDataset.get(0, "EPARCHY_CODE"));
				assureCustDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryCustInfoByCustId", param4CustQry);
				if (assureCustDataset.isEmpty())
				{
					CSViewException.apperr(CustException.CRM_CUST_134, data.getString("SERIAL_NUMBER"));
				}
				String custName =  String.valueOf(assureCustDataset.get(0, "CUST_NAME"));
				param.put("CUST_NAME",  custName);
				IDataset resdata = CSViewCall.call(this, "SS.TopSetBoxSVC.updateModem", param);
			}else{
				String resultInfo = crmdataSet.first().getString("X_RESULTINFO", "crm调用异常！[光猫]");
				CSViewException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
			}
		}
		this.setInfo(retData);
		this.setAjax(retData);
		setAjax("DEAL_SUCCESS", "TRUE");
	}
}
