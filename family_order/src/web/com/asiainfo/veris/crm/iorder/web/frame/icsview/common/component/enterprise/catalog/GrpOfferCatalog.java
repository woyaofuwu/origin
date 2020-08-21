package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.catalog;

import org.apache.log4j.Logger;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class GrpOfferCatalog extends BizTempComponent
{
	private static final Logger logger = Logger.getLogger(GrpOfferCatalog.class);
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/catalog/GrpOfferCatalog.js";

        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }

        IData param = getPage().getData();
        String action = param.getString("ACTION");
        if("INIT_CATALOG".equals(action))
        {
            queryOfferCatalogList(param);
        }
        else if("LOAD_OFFER_LIST".equals(action))
        {
            queryOfferListByCatalogId(param);
        }
    }
    
    private void queryOfferCatalogList(IData param) throws Exception
    {
        String custId = param.getString("CUST_ID");
        String operType = param.getString("OPER_TYPE");
        IDataset catalogList;
        if("20".equals(operType))
        {//开通
            catalogList = IUpcViewCall.queryCatalogsByUpCatalogId(getUpCatalogId());
            catalogList = filterOfferCatalog(catalogList, param);
        }
        else
        {//变更&注销
            IData input = new DataMap();
            input.put("CUST_ID", custId);
            input.put("UP_CATALOG_ID", getUpCatalogId());
            catalogList = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.loadOfferCatalogForOrdered", input);
            catalogList = filterOfferCatalog(catalogList, param);
            if(IDataUtil.isNotEmpty(catalogList))
            {
                IData catalogOfferList = new DataMap();
                for(int i = 0, size = catalogList.size(); i < size; i++)
                {
                    IData catalog = catalogList.getData(i);
                    IDataset userProductList = catalog.getDataset("OFFER_LIST");
                    if(IDataUtil.isNotEmpty(userProductList))
                    {
                        catalogOfferList.put(catalog.getString("CATALOG_ID"), userProductList);
                    }
                }
                this.getPage().setAjax(catalogOfferList);
            }
        }
        setCatalogList(catalogList);
    }
    
    private void queryOfferListByCatalogId(IData param) throws Exception
    {
    	logger.debug("====queryOfferListByCatalogId====param==============="+param);
        String catalogId = param.getString("CATALOG_ID");
        IDataset offerList = IUpcViewCall.queryOffersByCatalogId(catalogId, getVisit().getLoginEparchyCode());
        
        if(IDataUtil.isNotEmpty(offerList) && StringUtils.isNotBlank(getFilterOfferSvc()))
        {
            IData data = getFilterOfferParam();
            if(IDataUtil.isNotEmpty(data))
            {
                param.putAll(data);
            }
            param.put("OFFER_LIST", offerList);
            offerList = CSViewCall.call(this, getFilterOfferSvc(), param);
        }
        setOfferList(offerList);
        this.getPage().setAjax(offerList);
    }
    
    private IDataset filterOfferCatalog(IDataset catalogList, IData param) throws Exception
    {
        if(IDataUtil.isNotEmpty(catalogList) && StringUtils.isNotBlank(getFilterCatalogSvc()))
        {
            IData data = getFilterCatalogParam();
            if(IDataUtil.isNotEmpty(data))
            {
                param.putAll(data);
            }
            param.put("CATALOG_LIST", catalogList);
            catalogList = CSViewCall.call(this, getFilterCatalogSvc(), param);
        }
        return catalogList;
    }
    
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        setOfferList(null);
        setCatalogList(null);
    }
    public abstract String getUpCatalogId() throws Exception;
    public abstract String getFilterCatalogSvc() throws Exception;
    public abstract IData getFilterCatalogParam() throws Exception;
    public abstract String getFilterOfferSvc() throws Exception;
    public abstract IData getFilterOfferParam() throws Exception;
    public abstract void setCatalogList(IDataset catalogList);
    public abstract void setOfferList(IDataset offerList);
}
