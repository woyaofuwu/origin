
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.saleactive;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class SaleActive extends CSBizTempComponent
{
    public abstract String getEparchyCodeCompId();

    public abstract String getNeedCheck();

    public abstract String getNeedUserId();
    
    public abstract void setAfterSelectPackageEvent(String afterSelectPackageEvent);

    public abstract void setCampnTypes(IDataset campnTypes);

    public abstract void setHotRecommSales(IDataset hotRecommSales);

    public abstract void setGoods(IDataset goods);

    public abstract void setInfo(IData info);

    public abstract void setProducts(IDataset products);

    public abstract void setSaleInfos(IDataset saleInfos);

    public abstract void setTradeTypeCode(String tradeTypeCode);

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding()) {
            setGoods(null);
            setHotRecommSales(null);
            setInfo(null);
            setProducts(null);
            setAfterSelectPackageEvent(null);
            setSaleInfos(null);
            setTradeTypeCode(null);
        }
    }
    
    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        addResourceFile(writer, "scripts/iorder/icsserv/component/saleactive/saleactive.js");
        String specTag = getPage().getParameter("SPEC_TAG");
        if (StringUtils.isBlank(specTag)) {
            readerComponentAction();
        } else if ("queryValidSaleActives".equals(specTag)) {
            queryValidSaleActives();
        } else if ("queryHotRecommSaleActives".equals(specTag)) {
            queryHotRecommSaleActives();
        } else if ("queryAllProducts".equals(specTag)) {
            queryAllProducts();
        } else if ("refreshProduct".equals(specTag)) {
            refreshProduct();
        } else if ("checkByProduct".equals(specTag)) {
            checkByProduct();
        } else if ("giftSaleActive".equals(specTag)) {
            giftSaleActive();
        } else if ("checkShortCutActive".equals(specTag)) {
            checkShortcutActive();
        } else if ("checkCreditPurchases".equals(specTag)){
        	checkCreditPurchases();
        }
    }
    
    private void readerComponentAction() throws Exception
    {
        String userId = getPage().getParameter("USER_ID", "");
        String custId = getPage().getParameter("CUST_ID", "");
        String acctDay = getPage().getParameter("ACCT_DAY", "");
        String firstDate = getPage().getParameter("FIRST_DATE", "");
        String nextAcctDay = getPage().getParameter("NEXT_ACCT_DAY", "");
        String nextFirstDate = getPage().getParameter("NEXT_FIRST_DAY", "");
        String needUserId = getPage().getParameter("NEED_USER_ID", getNeedUserId());
        String eparchyCode = getPage().getParameter("EPARCHY_CODE", "");
        String tradeTypeCode = getPage().getParameter("TRADE_TYPE_CODE", "");

        String mulChannelTag = getPage().getParameter("MUL_CHANNEL_TAG", "0");
        IData info = new DataMap();
        info.put("MUL_CHANNEL_TAG", mulChannelTag);

        if ("true".equals(needUserId) && StringUtils.isBlank(userId)) {
            info.put("NEED_DISABLED", "true");
        }

        if (StringUtils.isNotBlank(eparchyCode)) {
            info.put("USER_ID", userId);
            info.put("CUST_ID", custId);
            info.put("ACCT_DAY", acctDay);
            info.put("FIRST_DATE", firstDate);
            info.put("NEXT_ACCT_DAY", nextAcctDay);
            info.put("NEXT_FIRST_DATE", nextFirstDate);

            IData cond = new DataMap();
            cond.put("EPARCHY_CODE", eparchyCode);
            cond.put("USER_ID", userId);
            cond.put("LABEL_ID", getPage().getParameter("LABEL_ID"));
            cond.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

            IDataset campnTypes;
            if("1".equals(mulChannelTag)){
                //泛渠道页面只显示信用购机
                campnTypes = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryCreditPurchasesCampnTypes", cond);
            }else {
                campnTypes = CSViewCall.call(this, "CS.SaleActiveQuerySVC.getCampnTypes", cond);
            }
            this.setCampnTypes(campnTypes);
        }
        setTradeTypeCode(tradeTypeCode);
        setInfo(info);
    }

    /**
     * 查询用户已有的营销活动
     * @throws Exception
     */
    private void queryValidSaleActives() throws Exception {
        IDataset SaleActives = new DatasetList();
        IData param = getPage().getData();
        // 查询非签约类营销活动入参
        param.put("QRY_TYPE", "2");
        param.put("ALL_FLAG", "false");

        String userId = param.getString("USER_ID", "");
        if (StringUtils.isNotBlank(userId)) {
            // 查询签约类营销活动
            IDataset QYSaleActives = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSaleActiveInfo", param);
            // 查询非签约类营销活动
            IDataset NOQYSaleActives = CSViewCall.call(this, "SS.QuerySaleActiveSVC.querySaleActive", param);
            if (IDataUtil.isNotEmpty(QYSaleActives)) {
                SaleActives.addAll(QYSaleActives);
            }
            if (IDataUtil.isNotEmpty(NOQYSaleActives)) {
                SaleActives.addAll(NOQYSaleActives);
            }
        }
        setSaleInfos(SaleActives);
    }

    /**
     * 查询热门和推荐营销活动
     * @throws Exception
     */
    private void queryHotRecommSaleActives() throws Exception {
        IDataset hotRecommSales = new DatasetList();
        IData param = new DataMap();
        param.put("POPUTRADECODE", "2"); // 仅查询营销活动类
        param.put("STAFF_ID", getVisit().getStaffId());
        IData saleInfos = CSViewCall.callone(this, "CS.WelcomeSVC.getHotAndRecInfo", param);
        if (IDataUtil.isNotEmpty(saleInfos)) {
            IDataset hotSales = saleInfos.getData("HOTINFOS").getDataset("SALES");
            IDataset recommSales = saleInfos.getData("RECINFOS").getDataset("SALES");
            if (IDataUtil.isNotEmpty(hotSales))
                hotRecommSales.addAll(hotSales);
            if (IDataUtil.isNotEmpty(recommSales))
                hotRecommSales.addAll(recommSales);
        }
        setHotRecommSales(hotRecommSales);
    }

    /**
     * 查询用户可以办理的所有营销方案
     * @throws Exception
     */
    private void queryAllProducts() throws Exception {
        setRenderContent(false);
        IData param = getPage().getData();
        String mulChannelTag = getPage().getParameter("MUL_CHANNEL_TAG", "0");
        IDataset products;
        if("1".equals(mulChannelTag)){
            products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryCreditPurchasesProducts", param);
        }else{
            products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryAllAvailableProducts", param);
        }

        getPage().setAjax(products);
    }
    
    private void refreshProduct() throws Exception
    {
        setRenderContent(false);
        String campnType = getPage().getParameter("CAMPN_TYPE");
        if (StringUtils.isBlank(campnType)) {
            return;
        }
        IData param = getPage().getData();
        String mulChannelTag = getPage().getParameter("MUL_CHANNEL_TAG", "0");
        IDataset products;
        if("1".equals(mulChannelTag)){
            products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryCreditPurchasesProductsByLabel", param);
        }else{
            products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryProductsByLabel", param);
        }

        getPage().setAjax(products);
    }
    
    private void checkByProduct() throws Exception
    {
    	String userId = getPage().getParameter("USER_ID", "");
        String custId = getPage().getParameter("CUST_ID", "");
        String productId = getPage().getParameter("PRODUCT_ID", "");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("PRODUCT_ID", productId);
        IDataset results = CSViewCall.call(this, "CS.SaleActiveQuerySVC.choiceProductNode", param);
    }
    
    private void checkCreditPurchases() throws Exception
    {
    	String userId = getPage().getParameter("USER_ID", "");
        String custId = getPage().getParameter("CUST_ID", "");
        String productId = getPage().getParameter("PRODUCT_ID", "");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("PRODUCT_ID", productId);
        IData results = CSViewCall.call(this, "CS.SaleActiveQuerySVC.checkCreditPurchases", param).first();
        
        IData result = new DataMap();
        result.put("CREDIT_PURCHASES", results.getString("CREDIT_PURCHASES"));

        getPage().setAjax(result);

    }
    
    
    private void giftSaleActive() throws Exception
    {
        String productId = getPage().getParameter("PRODUCT_ID", "");
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        IDataset results = CSViewCall.call(this, "CS.SaleActiveQuerySVC.checkGiftSaleactive", param);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(results)) {
        	result.put("productAttr", "1");
        } else {
        	result.put("productAttr", "0");
        }
        getPage().setAjax(result);
    }
    
    private void checkShortcutActive() throws Exception
    {
        String newImei = getPage().getParameter("NEW_IMEI");
        String productId = getPage().getParameter("PRODUCT_ID");
        String packageId = getPage().getParameter("PACKAGE_ID");
        String eparchyCode = getPage().getParameter("EPARCHY_CODE");
        String serialNumber = getPage().getParameter("SERIAL_NUMBER");
        String campnType = getPage().getParameter("CAMPN_TYPE");

        IData param = new DataMap();

        param.put("NEW_IMEI", newImei);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("CAMPN_TYPE", campnType);

        IDataset actives = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleActivesByImei", param);

        if (IDataUtil.isEmpty(actives)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该型终端不能办理该营销活动！");
        }
    }
}
