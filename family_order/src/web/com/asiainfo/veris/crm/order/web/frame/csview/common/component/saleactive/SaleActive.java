
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.saleactive;

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

public abstract class SaleActive extends CSBizTempComponent
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
            setCommonSaleList(null);
            setGoods(null);
            setHotSaleList(null);
            setInfo(null);
            setProducts(null);
            setAfterChoicePackageEvent(null);
        }
    }

    public abstract String getEparchyCodeCompId();

    public abstract String getNeedCheck();

    public abstract String getNeedUserId();

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
            IDataset shortcutList = CSViewCall.call(this, "CS.SaleActiveQuerySVC.queryShortCutSaleActive", cond);
            IDataset commonSaleList = new DatasetList();
            IDataset hotSaleList = new DatasetList();
            if (IDataUtil.isNotEmpty(shortcutList))
            {
                int size = shortcutList.size();
                for (int i = 0; i < size; i++)
                {
                    IData shortcut = shortcutList.getData(i);
                    String packageStr = shortcut.getString("PRODUCT_ID") + "|" + shortcut.getString("PACKAGE_ID") + "|" + shortcut.getString("LABEL_ID");
                    shortcut.put("PACKAGE", packageStr);
                }
                for (int i = 0; i < size; i++)
                {
                    IData shortcut = shortcutList.getData(i);
                    if ("1".equals(shortcut.getString("SHORTCUT_TYPE", "")))
                    {
                        commonSaleList.add(shortcut);
                    }
                    else if ("2".equals(shortcut.getString("SHORTCUT_TYPE", "")))
                    {
                        hotSaleList.add(shortcut);
                    }
                }
            }

            setCommonSaleList(commonSaleList);
            setHotSaleList(hotSaleList);
            cond.put("USER_ID", userId);
            cond.put("LABEL_ID", getPage().getParameter("LABEL_ID"));
            cond.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IDataset campnTypes = CSViewCall.call(this, "CS.SaleActiveQuerySVC.getCampnTypes", cond);
            this.setCampnTypes(campnTypes);
        }

        setInfo(info);
    }
    
    private void checkByProduct(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
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
    
    private void giftSaleactive(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        String productId = getPage().getParameter("PRODUCT_ID", "");
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        IDataset results = CSViewCall.call(this, "CS.SaleActiveQuerySVC.checkGiftSaleactive", param);
        IData result = new DataMap();
        if(IDataUtil.isNotEmpty(results))
        {
        	result.put("productAttr", "1");
        }
        else
        {
        	result.put("productAttr", "0");
        }
        getPage().setAjax(result);
    }

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
    }

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        addResourceFile(writer, "scripts/csserv/component/saleactive/saleactive.js");
        String specTag = getPage().getParameter("SPEC_TAG");
        if (StringUtils.isBlank(specTag))
        {
            readerComponentAction(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("refreshProduct"))
        {
            refreshProduct(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("checkByProduct"))
        {
        	checkByProduct(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("giftSaleactive"))
        {
        	giftSaleactive(informalParametersBuilder, writer, cycle);
        }
        else if (specTag.equals("checkShotCutActive"))
        {
            checkShortcutActive(informalParametersBuilder, writer, cycle);
        }
    }

    public abstract void setAfterChoicePackageEvent(String afterChoicePackageEvent);

    public abstract void setCampnTypes(IDataset campnTypes);

    public abstract void setCommonSale(IData commonSale);

    public abstract void setCommonSaleList(IDataset commonSaleList);

    public abstract void setGoods(IDataset goods);

    public abstract void setHotSale(IData hotSale);

    public abstract void setHotSaleList(IDataset hotSaleList);

    public abstract void setInfo(IData info);

    public abstract void setProducts(IDataset products);
}
