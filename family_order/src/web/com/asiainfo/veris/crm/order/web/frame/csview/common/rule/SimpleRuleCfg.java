
package com.asiainfo.veris.crm.order.web.frame.csview.common.rule;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SimpleRuleCfg extends PersonBasePage
{
    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("SCRIPT_PATH", "com.ailk.personserv.service.rule.");
        data.put("SCRIPT_METHOD", "run");
        setEditInfo(data);

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        CSViewCall.call(this, "CS.SimpleRuleCfgSVC.createSimpleRule", data);
    }

    public abstract void setEditInfo(IData editInfo);
}
