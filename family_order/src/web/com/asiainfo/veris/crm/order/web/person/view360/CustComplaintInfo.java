
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CustComplaintInfo extends PersonBasePage
{

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.initCustComplaint", data);
        if (IDataUtil.isNotEmpty(output))
        {
            returnData = output.getData(0);
        }
        setCond(returnData);
    }

    /**
     * 用户投诉信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryCustComplaintInfo", data, getPagination("CustComplaintInfo"));

        setCond(data);
        setInfos(output.getData());
        setCondition(output.getData().first());
        setInfosCount(output.getDataCount());
    }

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
