
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dmparamrequest;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ParamRequest extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();
        IData userInfo = new DataMap(pageData.getString("USER_INFO"));
        pageData.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));

        pageData.putAll(userInfo);

        IDataset infos = CSViewCall.call(this, "SS.ParamRequestSVC.getBusiList", pageData);
        IDataset facts = CSViewCall.call(this, "SS.ParamRequestSVC.getFactoryList", pageData);

        setInfos(infos);
        setFacts(facts);

    }

    /**
     * 提交
     * 
     * @data 2013-8-14
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        IDataset dataset = CSViewCall.call(this, "SS.ParamRequestSVC.sendTuxGather", pageData);

        if (IDataUtil.isEmpty(dataset))
        {

            // 暂时注释
            CSViewException.apperr(DMBusiException.CRM_DM_152);
        }
        setAjax(dataset);
    }

    /**
     * 发送参数配置请求
     * 
     * @data 2013-8-14
     * @param cycle
     * @throws Exception
     */
    public void sendHttpGather(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();

        IDataset dataset = CSViewCall.call(this, "SS.ParamRequestSVC.sendHttpGather", pageData);
        getContext().setAjaxCheck(dataset.getData(0).toString(), "操作成功标识与FUNCTIONID,TERMINALID");
    }

    /**
     * @data 2013-8-14
     * @param cycle
     * @throws Exception
     */
    public void sendHttpTerm(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();
        IDataset terms = CSViewCall.call(this, "SS.ParamRequestSVC.getTermByHttp", pageData);
        setTerms(terms);

    }

    public abstract void setFacts(IDataset facts);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRowIndex(int index);

    public abstract void setTerms(IDataset terms);

}
