
package com.asiainfo.veris.crm.order.web.person.sundryquery.prosecution;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：垃圾短信查询 作者：GongGuang
 */
public abstract class QueryProsecution extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 功能：初始化开始、结束日期
     */
    public void qryInit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IData result = CSViewCall.callone(this, "SS.QueryProsecutionSVC.queryInitCondition", new DataMap());
        if (IDataUtil.isNotEmpty(result))
        {
            data.put("cond_START_DATE", result.getString("START_DATE")); // 得到本月的第一天
            data.put("cond_END_DATE", result.getString("END_DATE"));// 格式为YYYY-MM-DD
            setCond(data);
        }
    }

    /**
     * 功能：垃圾短信查询结果
     */
    public void queryProsecution(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryProsecutionSVC.queryProsecution", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【垃圾短信】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        inparam.put("cond_EXPORT_TAG", "1");
        setInfos(dataCount.getData());
        setCount(dataCount.getDataCount());
        setCond(getData("cond", true));
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
