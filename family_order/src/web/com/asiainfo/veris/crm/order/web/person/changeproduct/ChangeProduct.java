
package com.asiainfo.veris.crm.order.web.person.changeproduct;


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


public abstract class ChangeProduct extends PersonBasePage
{

    public void changeProductTipsInfo(IRequestCycle cycle) throws Exception
    {
        IData infos = new DataMap();

        IData data = getData();

        IDataset choiceInfos = CSViewCall.call(this, "SS.ChangeProductSVC.changeProductTipsInfo", data);

        if (IDataUtil.isNotEmpty(choiceInfos))
        {
            infos.put("TIPS_TYPE_CHOICE", choiceInfos);
        }

        setAjax(infos);
    }

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

        IDataset result = CSViewCall.call(this, "SS.ChangeProductSVC.afterSubmitSnTipsInfo", data);

        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                infos.getData(0).getDataset("TIPS_TYPE_TIP").add(result.getData(i));
            }
        }

        setAjax(infos.getData(0));
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
        cond.put("VPMN_USER_ID_A", this.getParameter("VPMN_USER_ID_A", ""));
        cond.put("VPMN_PRODUCT_ID", this.getParameter("VPMN_PRODUCT_ID", ""));

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
        String custId = this.getParameter("CUST_ID");
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("CUST_ID", custId);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getParameter(Route.ROUTE_EPARCHY_CODE));
        IDataset result = CSViewCall.call(this, "SS.ChangeProductSVC.loadChildInfo", data);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAjax(result);
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-5 上午10:13:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 chengxf2 v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pgData = this.getData();
        String tradeTypeCode = pgData.getString("TRADE_TYPE_CODE", "110");
        String authType = pgData.getString("authType", "00");
        IData info = new DataMap();
        info.put("TRADE_TYPE_CODE", tradeTypeCode);
        info.put("authType", authType);
        this.setInfo(info);
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
                searchData.put("PRODUCT_ID_A", productId);
                SearchResponse resp = SearchClient.search("PM_OFFER_JOIN_REL", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();
                
                datas.addAll(resp.getDatas());
                ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
            else if ("2".equals(searchType))
            {
                // 元素搜索
                Map<String, String> searchData = new HashMap<String, String>();
                searchData.put("PRODUCT_ID", productId);
                SearchResponse resp = SearchClient.search("PM_OFFER_ELEMENT", searchText, searchData, 0, 10);
                IDataset datas = resp.getDatas();
                datas.addAll(resp.getDatas());
                ElementPrivUtil.filterElementListByPriv(this.getVisit().getStaffId(), datas);
                this.setAjax(datas);
            }
        }
    }
    
    public void checkShareMealPhoneNum(IRequestCycle cycle) throws Exception
    {
    	IData userData = getData(); 
        IDataset results = CSViewCall.call(this, "SS.ChangeProductSVC.checkShareMealPhoneNum", userData);
        setAjax(results.first());
    }
    
    
    

    public abstract void setInfo(IData info);

    public abstract void setPackageId(String packageId);

    public abstract void setUserProductId(String userProductId);

    public abstract void setVpmnDiscnt(IDataset vpmnDiscnt);

    public void submitChangeProduct(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
    	CSViewCall.call(this, "SS.ChangeProductIntfSVC.checkPwlwApnName", data);
        
        //从前台渠道进入的标识
        data.put("IS_FROM_FOREGROUND", "1");
        /*
         * CSViewException.apperr(ProductException.CRM_PRODUCT_1);
         */
        IDataset result = CSViewCall.call(this, "SS.ChangeProductRegSVC.tradeReg", data);
        this.setAjax(result);
    }
}
