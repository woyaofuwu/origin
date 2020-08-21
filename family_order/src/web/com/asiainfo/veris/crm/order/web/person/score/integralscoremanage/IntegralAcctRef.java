
package com.asiainfo.veris.crm.order.web.person.score.integralscoremanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class IntegralAcctRef extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void getCommInfo(IRequestCycle cycle) throws Exception
    {
        IData indata = getData();
        indata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset data = CSViewCall.call(this, "SS.IntegralAcctRefSVC.getCommInfo", indata);
        IData comminfo = new DataMap();
        if (IDataUtil.isNotEmpty(data))
        {
            comminfo.put("USER_INTEGRAL_ACCT_ID", data.getData(0).getString("INTEGRAL_ACCT_ID"));
        }
        setCommInfo(comminfo);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.IntegralAcctRefRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    /**
     * 查询详细列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryScoreAcctList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput acctList = CSViewCall.callPage(this, "SS.IntegralAcctRefSVC.queryScoreAcctList", data, getPagination("acctRef"));
        setInfos(acctList.getData());
        setCount(acctList.getDataCount());
    }

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCond(IData condition);

    public abstract void setCount(long count);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setScoreAcctList(IDataset lists);

}
