
package com.asiainfo.veris.crm.order.web.person.sundryquery.userreservecombo;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：用户预约产品查询 作者：GongGuang
 */
public abstract class QueryUserReserveCombo extends PersonBasePage
{

    /**
     * 功能：初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 功能：用户预约产品查询结果
     */
    public void queryUserReserveCombo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        IData inparamBack = inparam;
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        // IDataset rsvTrades = CSAppCall.call("SS.QueryUserReserveComboSVC.queryUserReserveTrade", inparam);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QueryUserReserveComboSVC.queryUserReserveTrade", inparam, null);
        IDataset rsvTrades = dataCount.getData();
        String alertInfo = "";
        if (rsvTrades != null && rsvTrades.size() > 0)
        {
            IDataset rsvProducts = new DatasetList();
            IDataset rsvServices = new DatasetList();
            IDataset rsvDiscnts = new DatasetList();
            this.setRsvTrades(rsvTrades);
            for (Iterator it = rsvTrades.iterator(); it.hasNext();)
            {
                IData temp = (IData) it.next();
                // 导出时使用的数据
                inparamBack.put("cond_TRADE_ID", temp.get("TRADE_ID"));
                inparam.put("TRADE_ID", temp.get("TRADE_ID"));
                // 获取用户预约产品信息
                IDataOutput dataCountProduct = CSViewCall.callPage(this, "SS.QueryUserReserveComboSVC.queryUserReserveProduct", inparam, null);
                IDataset products = new DatasetList();
                if(dataCountProduct != null){
                	products = dataCountProduct.getData();
                	for (int i = 0; i < products.size(); i++) {
                		IData product = products.getData(i);
                		product.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", product.getString("PRODUCT_ID","")));
                		product.put("OLD_PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", product.getString("OLD_PRODUCT_ID","")));
                		product.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, product.getString("BRAND_CODE","")));
                		product.put("OLD_BRAND",UpcViewCall.getBrandNameByBrandCode(this, product.getString("OLD_BRAND_CODE","")));
					}
                }
                rsvProducts.addAll(products);
                // 获取用户预约服务信息
                IDataOutput dataCountService = CSViewCall.callPage(this, "SS.QueryUserReserveComboSVC.queryUserReserveService", inparam, null);
                IDataset services = new DatasetList();
                if(dataCountService != null){
                	services = dataCountService.getData();
                	for (int i = 0; i < services.size(); i++) {
                		IData service = services.getData(i);
                		service.put("SERVICE_NAME", UpcViewCall.queryOfferNameByOfferId(this, "S", service.getString("SERVICE_ID","")));
					}
                }
                rsvServices.addAll(services);
                // 获取用户预约优惠信息
                IDataOutput dataCountDiscnt = CSViewCall.callPage(this, "SS.QueryUserReserveComboSVC.queryUserReserveDiscnt", inparam, null);
                IDataset discnts = new DatasetList();
                if(dataCountDiscnt != null){
                	discnts = dataCountDiscnt.getData();
                	for (int i = 0; i < discnts.size(); i++) {
                		IData discnt = discnts.getData(i);
                		discnt.put("DISCNT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "D", discnt.getString("DISCNT_CODE","")));
					}
                }
                rsvDiscnts.addAll(discnts);
            }

            this.setRsvProducts(rsvProducts);
            this.setRsvServices(rsvServices);
            this.setRsvDiscnts(rsvDiscnts);

        }
        else
        {
            alertInfo = "没有符合查询条件的【用户预约产品】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setCondition(inparamBack);
    }

    public abstract void setCondition(IData condition);

    public abstract void setRsvDiscnts(IDataset rsvDiscnts);

    public abstract void setRsvProducts(IDataset rsvProducts);

    public abstract void setRsvServices(IDataset rsvServices);

    public abstract void setRsvTrades(IDataset rsvTrades);
}
