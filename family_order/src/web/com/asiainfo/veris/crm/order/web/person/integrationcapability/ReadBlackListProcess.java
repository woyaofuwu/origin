
package com.asiainfo.veris.crm.order.web.person.integrationcapability;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReadBlackListProcess extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(ReadBlackListProcess.class);

    public void submitProcess(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData("cond", true);

        String serialNum = getData().getString("AUTH_SERIAL_NUMBER", "");
        String operBusi = pageData.getString("SUBMIT_MODE2", "");
        String operType = pageData.getString("SUBMIT_MODE3", "");

        StringBuilder operateConditions = new StringBuilder();
        operateConditions.append(serialNum); // 号码
        operateConditions.append("|");
        operateConditions.append(operBusi); // 被屏蔽业务
        operateConditions.append("|");
        operateConditions.append(operType); // 操作类型
        operateConditions.append("|");
        operateConditions.append(getVisit().getStaffName()); // 操作人姓名
        operateConditions.append("|");
        operateConditions.append(getVisit().getStaffId()); // 操作人ID
        operateConditions.append("|");
        operateConditions.append("898"); // 省份ID

        IData inputData = new DataMap();
        inputData.put("KIND_ID", "BIP2C094_T2002094_0_0");
        inputData.put("CALLERNO", serialNum);// 号码
        inputData.put("HOMEPROV", "898");// 省代码
        inputData.put("SVCTYPEID", "0103030308");// 服务请求分类编码
        inputData.put("CONTACTCHANNEL", "08");// 受理渠道
        inputData.put("SERVICETYPEID", "42");// 业务类别
        inputData.put("OPERATETYPEID", "02001");// 操作类型
        inputData.put("SUBMIT_MODE3", operType);// 记录操作日志
        inputData.put("OPERATECONDITIONS", operateConditions.toString());

        IDataset infos = CSViewCall.call(this, "SS.BaseCommRecordSVC.subSubmitProcess", inputData);

        setCond(pageData);
        setInfo(infos.getData(0));
        setAjax(infos);
    }
    
    public abstract void setCond(IData cond);
    
    public abstract void setInfo(IData info);
}
