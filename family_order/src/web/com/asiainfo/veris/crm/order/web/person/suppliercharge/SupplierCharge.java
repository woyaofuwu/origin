/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.suppliercharge;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SupplierCharge extends PersonBasePage
{

    public abstract IDataset getFees();

    public abstract IDataset getMarkets();

    public abstract IDataset getShoppers();

    /**
     * 点击查询触发查找 费用列表数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData input = getData();

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
        param.putAll(input);

        IDataset dataset = CSViewCall.call(this, "SS.SupplierChargeSVC.queryFeeInfo", param);
        if (IDataUtil.isNotEmpty(dataset))
        {
            // 根据月份降序
            DataHelper.sort(dataset, "ACCEPT_MONTH", IDataset.TYPE_INTEGER, IDataset.ORDER_DESCEND);
            setFees(dataset);
            this.setAjax(dataset);
        }
    }

    /**
     * 页面初始化预载
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData input = getData();

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
        param.put("CITY_CODE", getVisit().getCityCode());

//        IDataset dataset = CSViewCall.call(this, "SS.SupplierChargeSVC.querySuppliers", param);
        IDataset dataset = CSViewCall.call(this, "SS.MobileShopManagerSvc.querySuppInfo", param);
		if(dataset != null && dataset.size() > 0){
			IData data = dataset.getData(0);
			this.setMarkets(data.getDataset("sellSupp"));
			this.setShoppers(data.getDataset("termSupp"));
			this.setAjax(data.getDataset("termSupp"));
		}
       /* if (IDataUtil.isNotEmpty(dataset))
        {
            IData data = dataset.getData(0);
            setMarkets(data.getDataset("MARKET_INFOS"));
            setShoppers(data.getDataset("SHOPPER_INFOS"));
            this.setAjax(data.getDataset("SHOPPER_INFOS"));
        }*/
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

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.SupplierChargeRegSVC.tradeReg", param);
        data.putAll(dataset.getData(0));
        setAjax(data);
    }
    
    /**
     * 打印收据
     * 
     * @param cycle
     * @throws Exception
     */
    public void printHandGathering(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.SupplierChargeSVC.printReceipt", data);
        setAjax(dataset);
    }

    public abstract void setFees(IDataset fees);

    public abstract void setMarkets(IDataset markets);

    public abstract void setShoppers(IDataset shoppers);
}
