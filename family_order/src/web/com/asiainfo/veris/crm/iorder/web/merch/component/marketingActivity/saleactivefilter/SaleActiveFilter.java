
package com.asiainfo.veris.crm.iorder.web.merch.component.marketingActivity.saleactivefilter;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class SaleActiveFilter extends CSBizTempComponent
{
    private void checkShortcutActive(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String newEmei = getPage().getParameter("NEW_EMEI");
        String productId = getPage().getParameter("PRODUCT_ID");
        String packageId = getPage().getParameter("PACKAGE_ID");
        String eparchyCode = getPage().getParameter("EPARCHY_CODE");
        String serialNumber = getPage().getParameter("SERIAL_NUMBER");
        String campnType = getPage().getParameter("CAMPN_TYPE");

        IData param = new DataMap();

        param.put("NEW_EMEI", newEmei);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("CAMPN_TYPE", campnType);

        IDataset actives = CSViewCall.call(this, "CS.SaleActiveQuerySVC.querySaleActivesByImei", param);

        if (IDataUtil.isEmpty(actives))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该型终端不能办理该营销活动！");
        }
    }

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setGoods(null);
            setInfo(null);
            setProducts(null);
            setCampnTypes(null);
            setAfterChoicePackageEvent(null);
        }
    }

    public abstract String getEparchyCodeCompId();

    public abstract String getNeedCheck();

    public abstract String getNeedUserId();

    public abstract String getNoCheck();

    public abstract String getQueryMode();

    public abstract String getCloseHotActive();

    /**
     * 查询营销活动基本信息，活动类型/热门活动/普通活动
     * 
     * @param informalParametersBuilder
     * @param writer
     * @param cycle
     * @throws Exception
     */
    private void readerComponentAction(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String userId = getPage().getParameter("USER_ID", "");
        String custId = getPage().getParameter("CUST_ID", "");
        String acctDay = getPage().getParameter("ACCT_DAY", "");
        String firstDate = getPage().getParameter("FIRST_DATE", "");
        String nextAcctDay = getPage().getParameter("NEXT_ACCT_DAY", "");
        String nextFirstDate = getPage().getParameter("NEXT_FIRST_DAY", "");
        String needUserId = getPage().getParameter("NEED_USER_ID", getNeedUserId());
        String eparchyCode = getPage().getParameter("EPARCHY_CODE", "");
        IData info = new DataMap();

        if ("true".equals(needUserId) && StringUtils.isBlank(userId))
        {
            info.put("NEED_DISABLED", "true");
        }

        if (StringUtils.isNotBlank(eparchyCode))
        {
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
            IDataset campnTypes = CSViewCall.call(this, "CS.SaleActiveQuerySVC.getCampnTypes", cond);
            this.setCampnTypes(campnTypes);
        }
        setInfo(info);
    }

    /**
     * 根据营销类型查询营销产品
     * 
     * @param informalParametersBuilder
     * @param writer
     * @param cycle
     * @throws Exception
     */
    private void refreshProduct(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        setRenderContent(false);
        String campnType = getPage().getParameter("CAMPN_TYPE");
        if (StringUtils.isBlank(campnType))
        {
            return;
        }
        IData param = getPage().getData();
        IDataset products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryProductsByLabel", param);
        getPage().setAjax(products);
        setProducts(products);
    }

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        addResourceFile(writer, "scripts/merch/marketingActivity/saleactivefilter/saleactivefilter.js");
        String specTag = getPage().getParameter("SPEC_TAG");
        if (StringUtils.isBlank(specTag)) {
            readerComponentAction(informalParametersBuilder, writer, cycle);
        } else if ("queryValidSaleActives".equals(specTag)) {//查询用户已有的营销活动
            queryValidSaleActives();
        } else if ("queryHotRecommSaleActives".equals(specTag)) {
            queryHotRecommSaleActives();//查询热门和推荐营销活动
        } else if ("queryAllProducts".equals(specTag)) {//查询用户可以办理的所有营销方案
            queryAllProducts();
        } else if ("refreshProduct".equals(specTag)) {
            refreshProduct(informalParametersBuilder, writer, cycle);
        } else if ("checkByProduct".equals(specTag)) {
            checkByProduct();
        } else if ("giftSaleActive".equals(specTag)) {
            giftSaleActive();
        } else if ("checkShortCutActive".equals(specTag)) { 
            checkShortcutActive(informalParametersBuilder, writer, cycle);
        } else if ("checkCreditPurchases".equals(specTag)){
        	checkCreditPurchases();
        }else if("checkSaleBook".equals(specTag)){
        	checkSaleBook();
        }

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
        if (IDataUtil.isNotEmpty(results))
        {
            result.put("productAttr", "1");
        }
        else
        {
            result.put("productAttr", "0");
        }
        getPage().setAjax(result);
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
    /**
     * 查询用户可以办理的所有营销方案
     * @throws Exception
     */
    private void queryAllProducts() throws Exception {
        setRenderContent(false);
        IData param = getPage().getData();
        IDataset products = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryAllAvailableProducts", param);
        getPage().setAjax(products);
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
    
    private void checkSaleBook() throws Exception {
        IData param = getPage().getData();
        IData returnData=new DataMap();
        IDataset infos = CSViewCall.call(this, "SS.SaleActiveSVC.checkSaleBook", param);
        if (IDataUtil.isNotEmpty(infos))
        {
            returnData = infos.getData(0);
        }
        else
        {
            returnData.put("AUTH_BOOK_SALE", 0);
        }

        getPage().setAjax(returnData);        
    }

    public abstract void setAfterChoicePackageEvent(String afterChoicePackageEvent);

    public abstract void setCampnTypes(IDataset campnTypes);

    public abstract void setGoods(IDataset goods);

    public abstract void setInfo(IData info);

    public abstract void setProducts(IDataset products);
    
    public abstract void setSaleInfos(IDataset saleInfos);

}
