
package com.asiainfo.veris.crm.order.web.frame.csview.person.base;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class PersonQueryPage extends CSBasePage
{

    protected void getChildTradeInfo() throws Exception
    {

        // TODO Auto-generated method stub

    }

    protected void getMofficeBySN(String serialNumber) throws Exception
    {

        String routeEparchyCode;
        IData data = new DataMap();

        if (serialNumber != null && serialNumber.length() > 0)
        {
            data.put("SERIAL_NUMBER", serialNumber);

            IDataset results = CSViewCall.call(this, "CS.RouteInfoQrySVC.getEparchyCodeBySn", data);

            if (results != null && results.size() > 0)
            {
                IData result = results.getData(0);

                routeEparchyCode = result.getString("EPARCHY_CODE");

                // getVisit().setRouteEparchyCode(routeEparchyCode);
            }
        }
        else
        {
            // getVisit().setRouteEparchyCode(getVisit().getStaffEparchyCode());
        }
    }

    /**
     * 框架方法1：点击菜单调用，初始化页面装载页面参数
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        // AppCtx.setParameter("cond_SERIAL_NUMBER", getParameter("SERIAL_NUMBER", ""));

    }

    /**
     * 清空界面数据为初时状态 在下面三种情况下调用 1、输入号码后条件不符合 2、业务受理完成之后
     * 
     * @throws Exception
     */
    protected void reset() throws Exception
    {

        // td.clear();

        // td = null;
        // j2eely csBaseBean = null;
        
    }

}
