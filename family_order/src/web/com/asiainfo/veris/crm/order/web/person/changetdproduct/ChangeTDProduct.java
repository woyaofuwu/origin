
package com.asiainfo.veris.crm.order.web.person.changetdproduct;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeTDProduct extends PersonBasePage
{
    /**
     * 业务规则校验
     * 
     * @param data
     * @throws Exception
     */
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
        /*
         * IDataset result = CSViewCall.call(this, "SS.ChangeProductSVC.queryVpmnRecommInfo", data); if
         * (IDataUtil.isNotEmpty(result)) { infos.getData(0).getDataset("TIPS_TYPE_TIP").add(result.getData(0)); }
         */
        setAjax(infos.getData(0));
    }

    public void checkGgCard(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        cond.put("DEVELOP_NO", this.getParameter("CARD_NO", ""));
        cond.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
        this.setAjax(CSViewCall.call(this, "SS.SaleActiveCheckSVC.checkGgCard", cond));
    }

    /**
     * 获取新VPMN优惠、以及预约产品时间
     * 
     * @param cycle
     * @throws Exception
     */
    public void getNewVpmnDiscntBookProductDate(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        cond.clear();

        cond.put("NEW_PRODUCT_ID", this.getParameter("NEW_PRODUCT_ID", ""));
        cond.put("EPARCHY_CODE", this.getParameter("EPARCHY_CODE"));
        cond.put("OLD_VPMN_DISCNT", this.getParameter("OLD_VPMN_DISCNT", ""));
        cond.put("ACCT_DAY", this.getParameter("ACCT_DAY", ""));
        cond.put("FIRST_DATE", this.getParameter("FIRST_DATE", ""));

        IDataset returnResult = CSViewCall.call(this, "SS.ChangeProductSVC.getNewVpmnDiscntBookProductDate", cond);

        if (IDataUtil.isNotEmpty(returnResult) && IDataUtil.isNotEmpty(returnResult.getData(0).getDataset("NEW_VPMN_DISCNT")))
        {
            setVpmnDiscnt(returnResult.getData(0).getDataset("NEW_VPMN_DISCNT"));
        }

        setAjax(returnResult);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        String userId = this.getParameter("USER_ID");
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
        IDataset result = CSViewCall.call(this, "SS.ChangeProductSVC.loadChildInfo", data);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAjax(result);
        }
    }

    public void search(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String searchText = param.getString("SEARCH_NAME");
        String eparchyCode = param.getString("EPARCHY_CODE");
        String productId = param.getString("PRODUCT_ID");
        String searchType = param.getString("SEARCH_TYPE");
        if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2 && StringUtils.isNotBlank(eparchyCode) && StringUtils.isNotBlank(productId))
        {
            if ("1".equals(searchType))
            {
                // 产品搜索
                Map<String, String> searchData = new HashMap<String, String>();
                searchData.put("RELEASE_EPARCHY_CODE", eparchyCode);
                searchData.put("PRODUCT_ID_A", productId);
                SearchResponse resp = SearchClient.search("TD_S_PRODUCT_TRANS", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();

                searchData.put("RELEASE_EPARCHY_CODE", "ZZZZ");
                resp = SearchClient.search("TD_S_PRODUCT_TRANS", searchText, searchData, 0, 10);
                datas.addAll(resp.getDatas());
                ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
            else if ("2".equals(searchType))
            {
                // 元素搜索
                Map<String, String> searchData = new HashMap<String, String>();
                searchData.put("EPARCHY_CODE", eparchyCode);
                searchData.put("PRODUCT_ID", productId);
                SearchResponse resp = SearchClient.search("TD_B_PACKAGE_ELEMENT", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();

                searchData.put("EPARCHY_CODE", "ZZZZ");
                resp = SearchClient.search("TD_B_PACKAGE_ELEMENT", searchText, searchData, 0, 10);
                datas.addAll(resp.getDatas());
                ElementPrivUtil.filterElementListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
        }
    }

    public abstract void setInfo(IData info);

    public abstract void setPackageId(String packageId);

    public abstract void setUserProductId(String userProductId);

    public abstract void setVpmnDiscnt(IDataset vpmnDiscnt);

    public void submitChangeProduct(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put("TRADE_TYPE_CODE", "3803");
        data.put("ORDER_TYPE_CODE", "3803");
        IDataset result = CSViewCall.call(this, "SS.ChangeProductRegSVC.tradeReg", data);
        this.setAjax(result);
    }
}
