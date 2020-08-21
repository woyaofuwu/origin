
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dminfogather;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InfoGather extends PersonBasePage
{

    /**
     * @data 2013-8-10
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();

        IData userInfo = new DataMap(pageData.getString("USER_INFO"));
        userInfo.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        pageData.putAll(userInfo);

        IDataset dataset = CSViewCall.call(this, "SS.InfoGatherSVC.getBusiList", pageData);

        setInfos(dataset);

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        /*
         * //调用接口处理 前台处理转为后台 IDataset datasetIBoss = CSViewCall.call("SS.InfoGatherSVC.sendHttpGather", pageData);
         * pageData.put("comminfo_OPERATEID", datasetIBoss.getData(0).getString("OPERATEID"));
         * pageData.put("comminfo_FUNCTIONID", datasetIBoss.getData(0).getString("FUNCTIONID"));
         */

        IDataset dataset = CSViewCall.call(this, "SS.InfoGatherSVC.sendTuxGather", pageData);

        if (IDataUtil.isEmpty(dataset))
        {

            // 暂时注释
            CSViewException.apperr(DMBusiException.CRM_DM_152);
        }
        setAjax(dataset);
    }

    /**
     * @data 2013-8-10
     * @param cycle
     * @throws Exception
     */
    public void sendHttpGather(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();

        IDataset dataset = CSViewCall.call(this, "SS.InfoGatherSVC.sendHttpGather", pageData);
        getContext().setAjaxCheck(dataset.getData(0).toString(), "操作成功标识与FUNCTIONID");
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRowIndex(int index);

}
