
package com.asiainfo.veris.crm.order.web.person.sundryquery.badmessage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：不良短信查询 作者：GongGuang
 */
public abstract class QueryBadMessage extends PersonBasePage
{

    /**
     * 金额处理 分转化成元
     * 
     * @param str
     * @throws Exception
     */
    private String dataMoneyDeal(String str) throws Exception
    {
        String string = null;
        if (str == null || "".equals(str))
            str = "0";
        string = FeeUtils.Fen2Yuan(str);
        return string;
    }

    public abstract IDataset getInfos();

    /**
     * 功能：不良短信查询
     */
    public void queryBadMessage(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryBadMessageSVC.queryBadMessage", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【不良短信】数据~";
        }
        else
        {
            for (int i = 0; i < results.size(); i++)
            {
                results.getData(i).put("DISPOSEDARREARAGE", dataMoneyDeal(results.getData(i).getString("DISPOSEDARREARAGE")));
            }
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
