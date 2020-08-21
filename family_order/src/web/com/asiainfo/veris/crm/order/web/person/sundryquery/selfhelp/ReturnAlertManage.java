
package com.asiainfo.veris.crm.order.web.person.sundryquery.selfhelp;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReturnAlertManage extends PersonBasePage
{
    /**
     * 用户IMEI查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryReturnAlert(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        data.put("DISCNT_CODE", data.getString("cond_DISCNT_CODE"));
        data.put("RSRV_DATE1", data.getString("cond_RSRV_DATE1"));

        IDataOutput orderInfos = CSViewCall.callPage(this, "SS.ReturnAlertManageSVC.queryReturnAlert", data, getPagination("navt"));
        if (orderInfos != null)
        {
            setCount(orderInfos.getDataCount());
        }
        setInfos(orderInfos.getData());
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset viceInfos);
}
