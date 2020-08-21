
package com.asiainfo.veris.crm.iorder.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CheckUserInfoNew extends PersonBasePage {

    public void getCheckUserInfo(IRequestCycle cycle) throws Exception {
        IData param = getData();
        IDataOutput infos;
        String normalUserCheck = param.getString("NORMAL_USER_CHECK", "");
        String serialNumber = param.getString("SERIAL_NUMBER", "").trim();
        if ("".equals(serialNumber)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "手机号码丢失！");
            return;
        }
        if (normalUserCheck.equals("false")) {
            infos = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.getCheckUserInfo", param, getPagination("CheckUserInfoNav"));
        } else {
            return;
        }
        setInfos(infos.getData());
        IData condition = new DataMap();
        condition.put("USER360VIEW_VALIDTYPE", param.getString("USER360VIEW_VALIDTYPE"));
        condition.put("SERVICE_NUMBER", param.getString("SERVICE_NUMBER"));
        condition.put("PSPT_NUMBER", param.getString("PSPT_NUMBER"));
        condition.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
        condition.put("SERIAL_NUMBER", serialNumber);
        condition.put("NORMAL_USER_CHECK", normalUserCheck);
        setCondition(condition);

    }

    public abstract void setCondition(IData data);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}