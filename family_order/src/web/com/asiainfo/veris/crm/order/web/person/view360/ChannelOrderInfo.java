
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChannelOrderInfo extends PersonBasePage
{

    /**
     * 获取传递的初始参数(USER_ID和CUST_ID)
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset initWeb = CSViewCall.call(this, "SS.GetUser360ViewSVC.initTradeHistoryInfo", data);
        if (IDataUtil.isNotEmpty(initWeb))
        {
            setCondition(initWeb.getData(0));
        }
    }

    public void initSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setInit(data);
    }

    /**
     * 用户关系信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData dP = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryChannelOrderInfo", dP, getPagination("ChInfonav"));
        setCondition(dP);
        setInfos(output.getData());
        setInfosCount(output.getDataCount());
    }

    public abstract void setCondition(IData condition);// 存储查询条件

    public abstract void setInfos(IDataset infos);// 存储业务历史信息

    public abstract void setInfosCount(long infosCount);

    public abstract void setInit(IData init);

}
