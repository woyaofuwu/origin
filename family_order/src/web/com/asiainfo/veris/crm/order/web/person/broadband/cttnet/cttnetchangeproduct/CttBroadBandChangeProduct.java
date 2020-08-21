
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetchangeproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @Description: 铁通宽带产品变更
 * @version: v1.0.0
 */
public abstract class CttBroadBandChangeProduct extends PersonBasePage
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

        String userId = this.getParameter("USER_ID");
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
        IDataset result = CSViewCall.call(this, "SS.CttBroadBandChangeProductSVC.qryUserInfo", data);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAjax(result);
        }

    }

    // 三户信息
    public abstract void setCustInfo(IData custInfo);

    public abstract void setProductList(IDataset productList);

    public abstract void setUserInfo(IData userInfo);

    /**
     * 业务受理
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitChangeProduct(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        String routeEparchyCode = this.getParameter(Route.ROUTE_EPARCHY_CODE);
        if (routeEparchyCode == null)
        {
            routeEparchyCode = getVisit().getStaffEparchyCode();
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);

        IDataset result = CSViewCall.call(this, "SS.CttChangeProductRegSVC.tradeReg", data);
        this.setAjax(result);
    }

}
