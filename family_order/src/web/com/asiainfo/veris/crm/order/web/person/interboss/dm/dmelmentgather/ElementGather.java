
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dmelmentgather;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ElementGather extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        IData tempData = new DataMap();
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));

        String userEparchy = userInfo.getString("EPARCHY_CODE", "");
        if (!userEparchy.equalsIgnoreCase(getVisit().getStaffEparchyCode()))
        {
            CSViewException.apperr(DMBusiException.CRM_DM_120);
        }

        tempData.put("IN_MODE_CODE", getVisit().getInModeCode());

        setInfo(tempData);

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData condition = getData();
        IDataset dataset = CSViewCall.call(this, "SS.ElementGatherSVC.sendTuxGather", condition);

        if (IDataUtil.isEmpty(dataset))
        {
            CSViewException.apperr(DMBusiException.CRM_DM_152);

        }

        setAjax(dataset);
    }

    /**
     * @data 2013-8-26
     * @param cycle
     * @throws Exception
     */
    public void sendHttpGather(IRequestCycle cycle) throws Exception
    {

        IData condition = getData();
        String phone = condition.getString("PHONE", "");
        String imei = condition.getString("IMEI", "");

        if (StringUtils.isBlank(imei) || StringUtils.isBlank(phone))
        {
            CSViewException.apperr(DMBusiException.CRM_DM_153);
        }

        IDataset dataset = CSViewCall.call(this, "SS.ElementGatherSVC.sendHttpGather", condition);

        getContext().setAjaxCheck(dataset.getData(0).getString("OPERATEID"), "操作成功标识");

        // setAjax(dataset);
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRowIndex(int index);

}
