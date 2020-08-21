
package com.asiainfo.veris.crm.order.web.person.sundryquery.querygamerecord;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MarketEmailShield extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(MarketEmailShield.class);

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

        String UserLevel = pageData.getString("USER_LEVEL", "");// 用户级别
        String UserComp = pageData.getString("USER_COMP", "");// 用户所属公司
        String UserDept = pageData.getString("USER_DEPT", "");// 用户所属部门
        String RecommentedName = pageData.getString("RECOMMENTED_NAME", "");// 推荐人姓名
        String UserName = pageData.getString("USER_NAME", "");// 用户姓名
        String Remarks = pageData.getString("REMARKS", "");// 说明备注
        String serialNum = pageData.getString("PHONE_NUM", "");// 手机号码


        String provinceID = this.getProvinceID();

        StringBuilder operateConditions = new StringBuilder();
        operateConditions.append(UserLevel);
        operateConditions.append("|");
        operateConditions.append(UserComp);
        operateConditions.append("|");
        operateConditions.append(UserDept);
        operateConditions.append("|");
        operateConditions.append(RecommentedName);
        operateConditions.append("|");
        operateConditions.append(UserName);
        operateConditions.append("|");
        operateConditions.append(Remarks);
        operateConditions.append("|");
        operateConditions.append(serialNum);

        IData inputData = new DataMap();
        inputData.put("KIND_ID", "BIP2C094_T2002094_0_0");
        inputData.put("CALLERNO", serialNum);// 号码
        inputData.put("HOMEPROV", provinceID);// 号码
        inputData.put("SVCTYPEID", "01020601");// 服务请求分类编码
        inputData.put("CONTACTCHANNEL", "08");// 受理渠道
        inputData.put("SERVICETYPEID", "62");// 业务类别
        inputData.put("OPERATETYPEID", "02001");// 操作类型
        inputData.put("OPERATECONDITIONS", operateConditions.toString());

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subSubmitProcess", inputData);

        setCond(pageData);
        setAjax(infos);
    }
}
