
package com.asiainfo.veris.crm.order.web.person.sundryquery.userbilllist;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：用户预约产品查询 作者：GongGuang
 */
public abstract class QueryUserBillList extends PersonBasePage
{

    public void init(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        IDataset result = CSViewCall.call(this, "SS.QueryUserBillListSVC.init", inputData);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAreas(result);
        }
    }

    public void queryUserBillHisInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput ibplatDs = CSViewCall.callPage(this, "SS.QueryUserBillListSVC.queryUserBillHisInfo", inparam, getPagination("page"));
        IDataset results = ibplatDs.getData();
        if (IDataUtil.isEmpty(results))
        {

        }
        this.setListInfos(results);
        this.setListCount(ibplatDs.getDataCount());
    }

    public abstract void setAreas(IDataset areas);

    public abstract void setCondition(IData result);

    public abstract void setInfo(IData info);

    public abstract void setListCount(long count);

    public abstract void setListInfo(IData result);

    public abstract void setListInfos(IDataset results);

    public abstract void setTipInfo(String tipInfo);
}
