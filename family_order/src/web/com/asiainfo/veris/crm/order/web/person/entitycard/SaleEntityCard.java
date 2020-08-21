/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.entitycard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 下午02:53:33
 */
public abstract class SaleEntityCard extends PersonBasePage
{

    public void activeClick(IRequestCycle cycle) throws java.lang.Exception
    {

        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.EntityCardSVC.activeEntityCard", data);

        if (IDataUtil.isNotEmpty(results))
        {
            IData temp = results.getData(0);
            if (temp.getBoolean("ACTIVE_FLAG"))
            {
                this.setAjax("activeFlag", "1");
            }
            else
            {
                this.setAjax("activeFlag", "0");
            }
        }
        else
        {
            this.setAjax("activeFlag", "0");
        }

    }

    public void addClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.EntityCardSVC.getResInfo", data);

        IDataset set1 = results.getData(0).getDataset("TABLE1");
        IDataset set2 = results.getData(0).getDataset("TABLE2");

        this.setBasicInfos(set1);
        this.setSaleInfos(set2);

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        this.setCsValueCardDiscount(StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "csValueCardDiscount", StaffPrivUtil.PRIV_TYPE_FUNCTION));
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.SaleEntityCardRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public void queryEntityCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        Pagination page = getPagination("recordNav");

        IDataOutput result = CSViewCall.callPage(this, "SS.EntityCardSVC.qryBookInfo", data, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(data);
        setPageCount(result.getDataCount());

    }

    public abstract void setBasicInfos(IDataset dataset);

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData cond);

    public abstract void setCsValueCardDiscount(boolean can);

    public abstract void setInfos(IDataset dataset);

    public abstract void setPageCount(long count);

    public abstract void setSaleInfos(IDataset dataset);

}
