
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.productselect;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.wade.web.v4.tapestry.component.tree.TreeFactory;
import com.wade.web.v4.tapestry.component.tree.TreeParam;

public abstract class ProductSelect extends CSBizTempComponent
{

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        // this.setMonitor(null);
        // this.setProductTypeCode(null);
    }

    public abstract String getMonitor();

    public abstract String getCatalogId();
    
    public abstract String getServiceName();
    
    public abstract void setMonitor(String monitor);

    public abstract void setCatalogId(String productTypeCode);

    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/csserv/component/product/productselect/productselect.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/csserv/component/product/productselect/productselect.js");
        }
        String serviceName = this.getServiceName();
        IData data = this.getPage().getData();
        if(!data.containsKey("PRODUCT_TYPE_CODE")){
        	return;
        }
        if(StringUtils.isBlank(serviceName)){
        	serviceName = "CS.ProductTreeSVC.getProducts";
        }
        
        IData rst = CSViewCall.callone(this, serviceName, data);
        this.getPage().setAjax(rst);
    }
}
