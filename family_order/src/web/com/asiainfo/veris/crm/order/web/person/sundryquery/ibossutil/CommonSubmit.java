package com.asiainfo.veris.crm.order.web.person.sundryquery.ibossutil; 



import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class CommonSubmit extends PersonQueryPage {

	static transient final Logger logger = Logger.getLogger(CommonSubmit.class);

	public abstract void setCond(IData cond);

	public abstract void setFamilyInfo(IData data);
	
	

	/**
	 * 获取省代码
	 * 
	 * @param cycle
	 * @return
	 * @throws Exception
	 */
	public String getProvinceID() throws Exception {
		String provinceID = "";

		if ("XINJ".equals(getVisit().getProvinceCode())) {
			provinceID = "991";
		}
		if ("HAIN".equals(getVisit().getProvinceCode())) {
			provinceID = "898";
		}
		if ("HNAN".equals(getVisit().getProvinceCode())) {
			provinceID = "731";
		}
		if ("QHAI".equals(getVisit().getProvinceCode())) {
			provinceID = "971";
		}
		if ("SHXI".equals(getVisit().getProvinceCode())) {
			provinceID = "290";
		}
		if ("TJIN".equals(getVisit().getProvinceCode())) {
			provinceID = "220";
		}
		if ("YUNN".equals(getVisit().getProvinceCode())) {
			provinceID = "871";
		}
		return provinceID;
	}

	public void initPage(IRequestCycle cycle) throws Exception {
		IData data = getData();

		data.put("SUBMIT_MODE1", data.getString("SERIAL_NUMBER", ""));

		setFamilyInfo(data);
	}
	
	public abstract String operateConditions() throws Exception;
	public abstract String serviceTypeId()throws Exception;
	public abstract String operateTypeId()throws Exception;
	public abstract String svctypeId()throws Exception;
	public abstract String kindId()throws Exception;
	public IDataset setResult(IDataset infos)throws Exception{
		return infos;
	}
	
	public void submitProcess(IRequestCycle cycle) throws Exception {
		IData pageData = getData("cond", true);


		String provinceID = this.getProvinceID();

		String operateConditions = operateConditions();

		IData inputData = new DataMap();
		inputData.put("KIND_ID", kindId()==null?"BIP2C094_T2002094_0_0":kindId());
		inputData.put("CALLERNO", pageData.getString("SUBMIT_MODE1", ""));// 号码
		inputData.put("HOMEPROV", provinceID);// 省代码
		inputData.put("SVCTYPEID", svctypeId()==null?"0000000000":svctypeId());// 服务请求分类编码
		inputData.put("CONTACTCHANNEL", "0");// 受理渠道
		inputData.put("SERVICETYPEID", serviceTypeId());// 业务类别
		inputData.put("OPERATETYPEID", operateTypeId());// 操作类型
		inputData.put("SUBMIT_MODE2", pageData.getString("SUBMIT_MODE2", ""));// 此参数记录操作日志
		inputData.put("OPERATECONDITIONS", operateConditions);

		IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subSubmitProcess", inputData);
		IDataset result=setResult(infos);
		setCond(pageData);
		setAjax(result);
	}
}
