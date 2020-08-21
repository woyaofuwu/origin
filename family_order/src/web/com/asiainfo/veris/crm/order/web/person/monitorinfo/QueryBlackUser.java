
package com.asiainfo.veris.crm.order.web.person.monitorinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryBlackUser extends PersonQueryPage
{
    public void queryBlackUsers(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        Pagination page = this.getPagination("queryNav");

        IDataOutput output = CSViewCall.callPage(this, "SS.MonitorInfoQuerySVC.queryBlackUsers", data, page);
/*        if (IDataUtil.isNotEmpty(output.getData()))
        {
            setInfos(output.getData());
            setCount(output.getDataCount());
        }
        else
        {
            IData param = new DataMap();
            param.put("ALERT_INFO", "没有符合条件的数据!");
            this.setAjax(param);
        }*/
        if (IDataUtil.isEmpty(output.getData()))
        {
        	IData param = new DataMap();
            param.put("ALERT_INFO", "没有符合条件的数据!");
            this.setAjax(param);
        }

        setInfos(output.getData());
        setCount(output.getDataCount());
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
