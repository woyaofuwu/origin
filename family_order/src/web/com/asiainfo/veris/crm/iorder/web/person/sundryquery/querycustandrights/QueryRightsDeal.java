package com.asiainfo.veris.crm.iorder.web.person.sundryquery.querycustandrights;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryRightsDeal extends PersonBasePage {

	/**
	 * 查询全球通用户是否办理权益
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryRightDealInfo(IRequestCycle cycle) throws Exception {
		// 查询7172对应的权益CODE
		IData inparam = getData();
		String sernumber = inparam.getString("SERIAL_NUMBER");
		String param_attr = "7172";
		IData param = new DataMap();
		param.put("PARAM_ATTR", param_attr);
		IDataset results = CSViewCall.call(this,"SS.QueryCustAndRrightsSVC.queryRightNameByClass", param);
		String PARAM_CODE = results.getData(0).getString("PARAM_CODE");// 取到717171的值
        String right_name = results.getData(0).getString("PARA_CODE5");
		IData idata = new DataMap();
		idata.put("SERIAL_NUMBER", sernumber);
		idata.put("DISCNT_CODE", PARAM_CODE);
		IDataset inDataset = CSViewCall.call(this,
				"SS.QueryCustAndRrightsSVC.queryUserClassBySn", idata);
		for(int i = 0; i < inDataset.size(); i++){
			
			inDataset.getData(i).put("SERIAL_NUMBER", sernumber);
			inDataset.getData(i).put("PARAM_NAME", right_name);
		}
		String alertInfo = "";
		if (IDataUtil.isEmpty(inDataset)) {
			alertInfo = "该号码没有办理权益~~";
		}
	    this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		
		setInfos(inDataset);
		setCount(inDataset.size());
	}

	public abstract void setCondition(IData inparam);

	public abstract void setCount(long count);

	public abstract void setInfos(IDataset infos);

	public abstract void setInfo(IData info);
}
