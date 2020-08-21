
package com.asiainfo.veris.crm.order.web.person.sundryquery.uniontrans;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：异网转接查询 作者：GongGuang
 */
public abstract class QueryUnionTrans extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 功能：初始化
     */
    public void init(IRequestCycle cycle)
    {
    }

    /**
     * 功能：异网转接查询结果
     */
    public void queryUnionTrans(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryUnionTransSVC.queryUnionTrans", inparam, getPagination("navt"));
        String alertInfo = "";
        if (IDataUtil.isEmpty(dataCount.getData()))
        {
            alertInfo = "没有符合查询条件的【联通转接】数据~!";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

        setInfos(dataCount.getData());
        setCount(dataCount.getDataCount());
        setCond(getData("cond", true));
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
