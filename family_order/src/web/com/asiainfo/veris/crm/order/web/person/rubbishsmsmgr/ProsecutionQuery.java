
package com.asiainfo.veris.crm.order.web.person.rubbishsmsmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class ProsecutionQuery extends PersonQueryPage
{
    public void queryProsecution(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IDataOutput output = CSViewCall.callPage(this, "SS.ProsecutionQuerySVC.queryProsecution", data, this.getPagination("QueryNavBar"));
        IDataset result = output.getData();
        if (IDataUtil.isNotEmpty(result))
        {
            setInfos(result);
            setCount(output.getDataCount());
        }
        else
        {
            IData alertInfo = new DataMap();
            alertInfo.put("ALERT_INFO", "获取垃圾短信信息无数据！");
            setAjax(alertInfo);
        }
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
