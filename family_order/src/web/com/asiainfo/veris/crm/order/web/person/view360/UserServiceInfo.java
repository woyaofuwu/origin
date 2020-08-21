
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserServiceInfo extends PersonBasePage
{

    /**
     * 用户服务信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("1".equals(data.getString("SelectTag", "0")))
        {
            IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSvcInfo", data);
            setInfos(output);
        }
        else
        {
            // IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryUserSvcInfo", data,
            // getPagination("UserServiceNav"));
            // setInfos(output.getData());
            // setInfosCount(output.getDataCount());
            IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSvcInfo", data);
            setInfos(output);
        }

    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
