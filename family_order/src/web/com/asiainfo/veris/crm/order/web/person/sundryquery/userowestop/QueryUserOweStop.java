
package com.asiainfo.veris.crm.order.web.person.sundryquery.userowestop;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：欠费停拆机用户查询 作者：GongGuang
 */
public abstract class QueryUserOweStop extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 功能：初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 功能：欠费停机用户查询
     */
    public void queryUserOweStop(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryUserOweStopSVC.queryUserOweStop", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【欠费停拆机用户清单】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCond(getData("cond", true));
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
