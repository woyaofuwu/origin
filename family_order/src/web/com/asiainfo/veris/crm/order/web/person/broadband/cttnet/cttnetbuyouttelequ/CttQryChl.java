
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetbuyouttelequ;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttQryChl extends PersonBasePage
{

    /**
     * 代理商查询初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQryChl(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();

        setCond(cond);
    }

    /**
     * 代理商查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryChl(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();

        IDataOutput result = null;
        cond.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        if (StringUtils.isNotBlank(cond.getString("DEPART_ID")))
        {
            result = CSViewCall.callPage(this, "SS.CttQryBuyoutTelEquSVC.qryChl", cond, getPagination("pageinfo"));
        }
        else
        {
            result = CSViewCall.callPage(this, "SS.CttQryBuyoutTelEquSVC.qryChl2", cond, getPagination("pageinfo"));
        }
        setInfos(result.getData());
        setCount(result.getDataCount());
        setCond(cond);
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset dataset);

}
