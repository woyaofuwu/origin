
package com.asiainfo.veris.crm.order.web.person.sundryquery.baduserinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：不良信息用户资料查询 作者：GongGuang
 */
public abstract class QueryBadUserInfo extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 
     */
    public void qryInit(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("cond_HAS_DATA", false);
        setCond(data);
    }

    /**
     * 功能：不良信息用户资料查询
     */
    public void queryBadUserInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryBadUserInfoSVC.queryBadUserInfo", inparam, getPagination("navt"));
        setInfos(dataCount.getData());
        setCount(dataCount.getDataCount());
        if (dataCount.getData().size() == 0)
        {
            inparam.put("cond_HAS_DATA", "false");
        }
        else
        {
            inparam.put("cond_HAS_DATA", "true");
        }
        setCond(inparam);
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
