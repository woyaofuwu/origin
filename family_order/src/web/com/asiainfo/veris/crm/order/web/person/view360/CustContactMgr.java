
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CustContactMgr extends PersonBasePage
{

    /**
     * 用户接触信息明细
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryContactTrac(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        // IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryNewContactTrac", data,
        // getPagination("CustContactNav2"));
        IDataset outSet = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryNewContactTrac", data);

        setCond(data);
        setInfos(outSet);
        // setInfos(output.getData());
        // setInfosCount(output.getDataCount());
    }

    /**
     * 用户接触信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryContactMgr", data, getPagination("CustContactNav"));

        setCond(data);
        setInfos(output.getData());
        setInfosCount(output.getDataCount());
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.initCustContactMgr", data);
        if (IDataUtil.isNotEmpty(output))
        {
            returnData.clear();
            returnData = output.getData(0);
        }
        setCond(returnData);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
