
package com.asiainfo.veris.crm.order.web.person.vipserver;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AirportVipServerCancel extends PersonBasePage
{

    /**
     * 易登机服务返销--获取是否返销
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static IDataset getCancel() throws Exception
    {

        IDataset dataset = new DatasetList();

        DataMap data1 = new DataMap();
        data1.put("CANCEL_NAME", "是");
        data1.put("CANCEL_VALUE", "1");
        dataset.add(data1);

        DataMap data2 = new DataMap();
        data2.put("CANCEL_NAME", "否");
        data2.put("CANCEL_VALUE", "0");
        dataset.add(data2);

        return dataset;
    }

    /**
     * 易登机服务返销--获取是否使用了免费次数
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static IDataset getFreeNum() throws Exception
    {

        IDataset dataset = new DatasetList();

        DataMap data1 = new DataMap();
        data1.put("FREE_NAME", "是");
        data1.put("FREE_VALUE", "1");
        dataset.add(data1);

        DataMap data2 = new DataMap();
        data2.put("FREE_NAME", "否");
        data2.put("FREE_VALUE", "0");
        dataset.add(data2);

        return dataset;
    }

    public IDataset getAuthType() throws Exception
    {
        IDataset dataset = new DatasetList();

        DataMap data1 = new DataMap();
        data1.put("QUERY_NAME", "手机号码");
        data1.put("QUERY_VALUE", "1");
        dataset.add(data1);

        DataMap data2 = new DataMap();
        data2.put("QUERY_NAME", "VIP号码");
        data2.put("QUERY_VALUE", "2");
        dataset.add(data2);

        return dataset;
    }

    /**
     * 查询易登机服务返销信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAirportCancel(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String routeEparchyCode = this.getParameter(Route.ROUTE_EPARCHY_CODE);
        if (routeEparchyCode == null)
        {
            routeEparchyCode = getVisit().getStaffEparchyCode();
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        IDataset vipServerInfos = CSViewCall.call(this, "SS.AirportVipServerCancelSVC.queryAirportCancel", data);
        this.setInfos(vipServerInfos);
    }

    public abstract void setInfos(IDataset infos);

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitButton(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IDataset tableData = new DatasetList(data.getString("TABLE_DATA"));
        if (tableData.size() > 1)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "只能选择一条记录进行返销！");
        }
        IData chooseData = tableData.getData(0);
        String routeEparchyCode = this.getParameter(Route.ROUTE_EPARCHY_CODE);
        if (routeEparchyCode == null)
        {
            routeEparchyCode = getVisit().getStaffEparchyCode();
        }
        chooseData.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);

        IDataset dataset = CSViewCall.call(this, "SS.AirportVipServerCancelSVC.submitButton", chooseData);
        // setAjax(dataset);

    }

}
