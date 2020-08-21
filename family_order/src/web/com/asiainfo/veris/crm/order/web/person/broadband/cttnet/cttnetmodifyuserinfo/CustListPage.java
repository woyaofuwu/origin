
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetmodifyuserinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户数据修改前台控制处理
 */
public abstract class CustListPage extends PersonBasePage
{
    /**
     * 查询客户列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustList(IRequestCycle cycle) throws Exception
    {
        IData condition = getData();
        condition.put("CUST_TYPE", "0");
        condition.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset infos = CSViewCall.call(this, "SS.CustListSVC.queryCustList", condition);
        setInfos(infos);
        setCondition(condition);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);
}
