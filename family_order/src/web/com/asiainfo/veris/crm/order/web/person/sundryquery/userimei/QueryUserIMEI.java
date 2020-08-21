
package com.asiainfo.veris.crm.order.web.person.sundryquery.userimei;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryUserIMEI extends PersonBasePage
{
    /**
     * 用户IMEI查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserImei(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);
        data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        IDataOutput orderInfos = CSViewCall.callPage(this, "SS.QueryUserImeiSVC.queryUserImei", data, getPagination("navt"));
        IDataset results = orderInfos.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合条件的数据";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setCount(orderInfos.getDataCount());
        setInfos(orderInfos.getData());
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset viceInfos);
}
