package com.asiainfo.veris.crm.order.web.person.data;

import org.apache.tapestry.IRequestCycle;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DataDonate extends PersonBasePage{
	
	   private void getCommInfo(IData allInfo) throws Exception
	    {
	        IData inparam = new DataMap();
	        inparam.put("USER_ID", allInfo.getData("USER_INFO").getString("USER_ID"));
	        inparam.put("OPEN_MODE", allInfo.getData("USER_INFO").getString("OPEN_MODE"));
	        inparam.put("USER_STATE_CODESET", allInfo.getData("USER_INFO").getString("USER_STATE_CODESET"));
	        inparam.put("SERIAL_NUMBER", allInfo.getData("USER_INFO").getString("SERIAL_NUMBER", ""));
	        inparam.put("BRAND_CODE", allInfo.getData("USER_INFO").getString("BRAND_CODE", ""));
	        IDataset dataset = CSViewCall.call(this, "SS.DataDonateSVC.getCommInfo", inparam);
	        if(!dataset.isEmpty()){
	        	setDataInfos(dataset);
	        }
	    }

	    /**
	     * 查询后设置页面信息
	     */
	    public void loadChildInfo(IRequestCycle cycle) throws Exception
	    {
	        IData data = getData();

	        IData datainfo = new DataMap();

	        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
	        setUserInfo(userInfo);
	        datainfo.put("USER_INFO", userInfo);
	        // 获取子业务资料
	        getCommInfo(datainfo);

	    }

	    /**
	     * 业务提交
	     * 
	     * @param cycle
	     * @throws Exception
	     */
	    public void onTradeSubmit(IRequestCycle cycle) throws Exception
	    {
	        IData data = getData();
	        IData param = new DataMap();
	        param.put("REMARK", data.getString("comminfo_REMARK"));
	        param.put("DONATE_DATA", data.getString("comminfo_DONATE_DATA"));
	        param.put("OBJ_SERIAL_NUMBER", data.getString("objinfo_OBJ_SERIAL_NUMBER"));
	        param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
	        param.put("COMM_ID", data.getString("COMM_ID"));
	        param.put("DATA_TYPE", data.getString("DATA_TYPE"));
	        param.put("BALANCE", data.getString("BALANCE"));
	        param.put("EFFECTIVE_DATE", data.getString("EFFECTIVE_DATE"));
	        param.put("EXPIRE_DATE", data.getString("EXPIRE_DATE"));
	        param.put("DISCNT_CODE", data.getString("DISCNT_CODE"));
	        IDataset dataset = CSViewCall.call(this, "SS.DataDonateRegSVC.tradeReg", param);
	        setAjax(dataset);
	    }

	    public void queryObjCustInfo(IRequestCycle cycle) throws Exception
	    {
	        IData data = getData();
	        IData inparam = new DataMap();
	        inparam.put("SERIAL_NUMBER", data.getString("objinfo_OBJ_SERIAL_NUMBER"));

	        IDataset objAllInfo = CSViewCall.call(this, "SS.DataDonateSVC.queryObjCustInfo", inparam);

	        setObjUserInfo(objAllInfo.getData(0).getData("OBJ_USER_INFO"));
	        setObjCustInfo(objAllInfo.getData(0).getData("OBJ_CUST_INFO"));
	        setAjax(objAllInfo);
	    }

	    public abstract void setCommInfo(IData commInfo);

	    public abstract void setCondition(IData condition);

	    public abstract void setCustInfo(IData custInfo);

	    public abstract void setInfos(IDataset infos);
	    
	    public abstract void setDataInfos(IDataset dataInfos);

	    public abstract void setObjCustInfo(IData objCustInfo);

	    public abstract void setObjUserInfo(IData objUserInfo);

	    public abstract void setUserInfo(IData userInfo);
}
