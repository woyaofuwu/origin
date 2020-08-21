
package com.asiainfo.veris.crm.order.web.person.simcardmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QuerySelfCardFlow extends PersonBasePage
{

    public void delFlowInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset output = CSViewCall.call(this, "SS.SelfChangeCardFlowSVC.delFlowInfo", data);
        setAjax(output.getData(0));
    }

    /**
     * 查询后设置页面信息
     */
    public void querySelfCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        // 用户资料
        IDataOutput output = CSViewCall.callPage(this, "SS.SelfChangeCardFlowSVC.querySelfCard", data, this.getPagination());
        setInfos(output.getData());
        setCount(output.getDataCount());
        data.put("NOW_DATE", SysDateMgr.getSysTime());
        setAjax(data);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCount(long count);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset info);
}
