package com.asiainfo.veris.crm.order.web.person.sundryquery.phoneanime;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AbnormalUsers extends PersonBasePage {

	
	static transient final Logger logger = Logger.getLogger(AddPABlackList.class);
    /**
     * 获取省代码
     * 
     * @param cycle
     * @return
     * @throws Exception
     */
    public String getProvinceID() throws Exception
    {
        String provinceID = "";

        if ("XINJ".equals(getVisit().getProvinceCode()))
        {
            provinceID = "991";
        }
        if ("HAIN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "898";
        }
        if ("HNAN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "731";
        }
        if ("QHAI".equals(getVisit().getProvinceCode()))
        {
            provinceID = "971";
        }
        if ("SHXI".equals(getVisit().getProvinceCode()))
        {
            provinceID = "290";
        }
        if ("TJIN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "220";
        }
        if ("YUNN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "871";
        }
        return provinceID;
    }
    
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("PHONE_NUM", data.getString("SERIAL_NUMBER", ""));

        setCond(data);
    }

    public abstract void setCond(IData cond);
	public void submitProcess(IRequestCycle cycle) throws Exception
	{
		
		
		IData condParams =getData("cond", true);
		IData inputData = new DataMap();
		StringBuilder operateConditions = new StringBuilder();
		operateConditions.append(condParams.getString("PHONE_NUM", "")); //手机号码
		operateConditions.append("|");
		operateConditions.append(condParams.getString("OPERATION_TYPE", "")); //操作方式
		
	
		String serNumber = condParams.getString("PHONE_NUM", ""); //号码
		String svcTypeId = "0103030704"; //服务请求分类编码:其他
		
		String operateTypeId = "02004"; //操作类型
		
		inputData.put("KIND_ID", "BIP2C094_T2002094_0_0");
		inputData.put("CALLERNO", serNumber);
		inputData.put("SVCTYPEID", svcTypeId); //?
		inputData.put("HOMEPROV", this.getProvinceID());
		 inputData.put("CONTACTCHANNEL", "0");// 受理渠道
		 inputData.put("SERVICETYPEID", "95");
		 inputData.put("OPERATETYPEID", operateTypeId);
		 inputData.put("OPERATECONDITIONS", operateConditions.toString());

		 IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subSubmitProcess", inputData);

	        setCond(condParams);
	        setAjax(infos);
	}
}
