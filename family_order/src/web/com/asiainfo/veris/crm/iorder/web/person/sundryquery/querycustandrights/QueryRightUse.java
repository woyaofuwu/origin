package com.asiainfo.veris.crm.iorder.web.person.sundryquery.querycustandrights;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryRightUse extends PersonBasePage{
	/**
	 * 查询全球通用户是否办理权益
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryRightUseInfo(IRequestCycle cycle) throws Exception {
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
				"SS.QueryCustAndRrightsSVC.queryRightByUserid", idata);
		//补充需求新增获取权益标识，可变更次数，可使用次数，剩余使用次数
		IData rightParam = new DataMap();
		String DISCNT_CODE = PARAM_CODE;
		String RIGHT_ID ="1";
		rightParam.put("SERIAL_NUMBER", sernumber);
		rightParam.put("DISCNT_CODE", DISCNT_CODE);
		rightParam.put("RIGHT_ID", RIGHT_ID);
		IDataset outDataset = CSViewCall.call(this,"SS.BenefitCenterIntfSVC.queryBenefitRelDetail", rightParam);
		String REL_ID = outDataset.getData(0).getString("REL_ID");
		String REMAIN_CHANGE_NUM =outDataset.getData(0).getString("REMAIN_CHANGE_NUM");//可变更次数
		String REMAIN_USE_NUM =outDataset.getData(0).getString("REMAIN_USE_NUM");//剩余使用次数
		String RIGHT_OBJ = outDataset.getData(0).getString("RIGHT_OBJ");//可使用次数
		IData outputParam = new DataMap();
		outputParam.put("REL_ID", REL_ID);
		outputParam.put("REMAIN_CHANGE_NUM", REMAIN_CHANGE_NUM);
		outputParam.put("REMAIN_USE_NUM", REMAIN_USE_NUM);
		outputParam.put("RIGHT_OBJ", RIGHT_OBJ);
		for(int i = 0; i < inDataset.size(); i++){
			
			inDataset.getData(i).put("SERIAL_NUMBER", sernumber);
			inDataset.getData(i).put("PARAM_NAME", right_name);
			inDataset.getData(i).put("PARAM_CODE", PARAM_CODE);
		}
		String alertInfo = "";
		if (IDataUtil.isEmpty(inDataset)) {
			alertInfo = "该号码无权益使用情况~~";
		}
	    this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		
		setInfos(inDataset);
		setInfo(outputParam);
		setCount(inDataset.size());
	}

	public abstract void setCondition(IData inparam);

	public abstract void setCount(long count);

	public abstract void setInfos(IDataset infos);

	public abstract void setInfo(IData info);

}
