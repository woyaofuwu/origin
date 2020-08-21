
package com.asiainfo.veris.crm.order.web.person.npapplycancel;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InNpApplyCancel extends PersonBasePage
{
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.InNpApplyCancelSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 查询携入申请台账信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryNpApplyTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataset infos = CSViewCall.call(this, "SS.InNpApplyCancelSVC.getNpTradeInfos", data);
        String alert_info = "";
        if (IDataUtil.isEmpty(infos))
        {
            alert_info = "查询主台账中该号码无携入申请台账。";
        }

        this.setAjax("ALERT_INFO", alert_info);
        setInfos(infos);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
