
package com.asiainfo.veris.crm.order.web.person.sundryquery.userinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryNetChooseUser extends PersonBasePage
{

    public void init(IRequestCycle cycle) throws Exception
    {
        IData inparam = new DataMap();
        // inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset result = CSViewCall.call(this, "SS.SundryQrySVC.initCityArea", null);
        if (IDataUtil.isNotEmpty(result))
        {
            inparam = result.getData(0);
        }
        setCondition(inparam);

    }

    public void queryNetChooseUserInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput ibplatDs = CSViewCall.callPage(this, "SS.QueryNetChooseUserSVC.queryNetChooseUserInfo", inparam, getPagination("page"));
        IDataset results = ibplatDs.getData();
        if (IDataUtil.isEmpty(results))
        {

        }
        this.setListInfos(results);
        this.setListCount(ibplatDs.getDataCount());
    }

    public abstract void setCondition(IData result);

    public abstract void setInfo(IData info);

    public abstract void setListCount(long count);

    public abstract void setListInfo(IData result);

    public abstract void setListInfos(IDataset results);

    public abstract void setTipInfo(String tipInfo);
}
