
package com.asiainfo.veris.crm.order.web.person.sundryquery.specqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryChannelDeposit extends PersonBasePage
{

    /**
     * 代理商购机押金查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryChannelDeposit(IRequestCycle cycle) throws Exception
    {

        IData cond = getData();
        cond.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        String departcode = cond.getString("cond_DEPART_CODE");
        cond.put("DEPART_CODE", departcode.toUpperCase());
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.SundryQrySVC.queryChannelDeposit", cond);

        // 设置页面返回数据
        setInfos(result);
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

}
