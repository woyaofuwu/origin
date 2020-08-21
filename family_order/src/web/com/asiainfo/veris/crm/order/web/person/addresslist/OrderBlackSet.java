package com.asiainfo.veris.crm.order.web.person.addresslist; 

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OrderBlackSet extends PersonBasePage {

	static transient final Logger logger = Logger.getLogger(OrderBlackSet.class);

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

	public void submitProcess(IRequestCycle cycle) throws Exception {
		IData pageData = getData("cond", true);


		String provinceID = this.getProvinceID();

		StringBuilder operateConditions = new StringBuilder();
		String serialNum = pageData.getString("SUBMIT_MODE1", "");
        String actiontag = pageData.getString("SUBMIT_MODE2", "");
        String peroiddate = pageData.getString("SUBMIT_MODE3", "");
        if("".equals(actiontag)||actiontag == null){
            actiontag = "1";
        }
        if("1".equals(actiontag)&&("".equals(peroiddate)||peroiddate == null)){
            peroiddate = "20991231235959";
        }
		operateConditions.append(serialNum); //号码
		operateConditions.append("|");
		operateConditions.append(actiontag); //动作标志
		operateConditions.append("|");
		operateConditions.append(peroiddate); //黑名单有效期

		IData inputData = new DataMap();
		inputData.put("KIND_ID", "BIP2C094_T2002094_0_0");
		inputData.put("CALLERNO", pageData.getString("SUBMIT_MODE1", ""));// 号码
		inputData.put("HOMEPROV", provinceID);// 省代码
		inputData.put("SVCTYPEID", "01010549");// 服务请求分类编码
		inputData.put("CONTACTCHANNEL", "08");// 受理渠道
		inputData.put("SERVICETYPEID", "164");// 业务类别
		inputData.put("OPERATETYPEID", "02001");// 操作类型
		inputData.put("SUBMIT_MODE2", pageData.getString("SUBMIT_MODE2", ""));// 此参数记录操作日志
		inputData.put("OPERATECONDITIONS", operateConditions.toString());

		IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subSubmitProcess", inputData);

		setCond(pageData);
		setAjax(infos);

	}
}
