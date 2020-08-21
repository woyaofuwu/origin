
package com.asiainfo.veris.crm.order.web.person.sundryquery.interboss.dmbusisel;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DMBusiSel extends PersonBasePage
{
    private static final Logger logger = Logger.getLogger(DMBusiSel.class);

    /**
     * 获取新产品资料列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryDMBusi(IRequestCycle cycle) throws Exception
    {
        IData conParams = getData("cond", true);

        IDataset dataset = CSViewCall.call(this, "SS.DMBusiCommonQurySVC.queryDMBusi", conParams);

        IDataset spinfos = dataset.getData(0).getDataset("SP_INFO");
        IDataset dmBusi = dataset.getData(0).getDataset("DM_BUSI");

        setActiveInfos(spinfos);
        setAccountsInfos(dmBusi);
        setCondition(conParams);
    }

    /**
     * 获取根据主表记录关联子表 获取DM业务数据列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryDMBusi_Sub(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.DMBusiCommonQurySVC.queryDMBusi_Sub", data);

        IDataset spinfos = dataset.getData(0).getDataset("SP_INFO_SUB");
        IDataset dmBusi = dataset.getData(0).getDataset("DM_BUSI_SUB");

        setActiveInfos(spinfos);
        setAccountsInfos(dmBusi);
        data.put("cond_APPLY_TYPE", data.get("PA_APPLY_TYPE"));
        setCondition(data);
    }

    public abstract void setAccountsInfos(IDataset accountsInfos);

    public abstract void setActiveInfos(IDataset activeInfos);

    public abstract void setCondition(IData condition);

    /**
     * DM业务操作取消——业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset ajaxDataset = CSViewCall.call(this, "SS.DMBusiCommonQurySVC.submitTrade", data);

        setAjax(ajaxDataset);
    }
}
