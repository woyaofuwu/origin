
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetcontinuepay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @Description: 铁通宽带产品变更
 * @version: v1.0.0
 */
public abstract class CttBroadBandContinuePay extends PersonBasePage
{
    // public abstract void setBankCodeList(IDataset bankCodeList);

    /**
     * @Description: 根据规格获取产品
     */
    public void getProductBySpec(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset productList = CSViewCall.call(this, "SS.CttBroadbandSVC.getProductBySpec", data);
        this.setProductList(productList);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        IData data = new DataMap();
        String userId = pagedata.getString("USER_ID");
        data.put("USER_ID", userId);
        data.put("SERIAL_NUMBER", pagedata.getString("SERIAL_NUMBER"));
        String eparchyCode = this.getParameter(Route.ROUTE_EPARCHY_CODE);
        ;
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = getVisit().getStaffEparchyCode();
        }
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset dsResult = CSViewCall.call(this, "SS.CttBroadBandContinuePaySVC.getOldBroadBandInfo", data);
        IData commonInfo = dsResult.size() > 0 ? (IData) dsResult.get(0) : null;
        this.setWidenetInfo(commonInfo);

    }

    // 三户信息
    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setProductList(IDataset productList);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setWidenetInfo(IData info);

    /**
     * 业务受理
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitContinuePay(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        String routeEparchyCode = this.getParameter(Route.ROUTE_EPARCHY_CODE);
        if (routeEparchyCode == null)
        {
            routeEparchyCode = getVisit().getStaffEparchyCode();
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);

        IDataset result = CSViewCall.call(this, "SS.CttBroadBandContinuePayRegSVC.tradeReg", data);
        this.setAjax(result);
    }

}
