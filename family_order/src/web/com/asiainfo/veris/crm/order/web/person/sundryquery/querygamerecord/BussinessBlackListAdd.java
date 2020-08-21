
package com.asiainfo.veris.crm.order.web.person.sundryquery.querygamerecord;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BussinessBlackListAdd extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(BussinessBlackListAdd.class);

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
        IData pageData = getData("cond", true);

        String serialNum = pageData.getString("PHONE_NUM", "");// 手机号码
        String sheild_type = pageData.getString("SHEILD_TYPE", "");// 屏蔽类型
        String reason = pageData.getString("REASON", "");// 添加原因
        String operator = pageData.getString("OPERATOR", "");// 操作员
        String sheild_product_code = pageData.getString("SHEILD_PRODECT_CODE", "");// 屏蔽产品编码


        String provinceID = this.getProvinceID();

        StringBuilder operateConditions = new StringBuilder();

        operateConditions.append(serialNum);
        operateConditions.append("|");
        operateConditions.append(sheild_type);
        operateConditions.append("|");
        operateConditions.append(reason);
        operateConditions.append("|");
        operateConditions.append(operator);
        operateConditions.append("|");
        operateConditions.append(sheild_product_code);

        IData inputData = new DataMap();
        inputData.put("KIND_ID", "BIP2C094_T2002094_0_0");
        inputData.put("CALLERNO", serialNum);// 号码
        inputData.put("HOMEPROV", provinceID);// 号码
        inputData.put("SVCTYPEID", "01020699");// 服务请求分类编码
        inputData.put("CONTACTCHANNEL", "08");// 受理渠道
        inputData.put("SERVICETYPEID", "130");// 业务类别
        inputData.put("OPERATETYPEID", "02001");// 操作类型
        inputData.put("OPERATECONDITIONS", operateConditions.toString());

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subSubmitProcess", inputData);

        setCond(pageData);
        setAjax(infos);
    }
}
