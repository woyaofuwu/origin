
package com.asiainfo.veris.crm.iorder.web.merch.component.shoppingcart;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ShoppingList extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
    }

    public abstract String getRenderCallSvc();

    @Override
    public void renderComponent(StringBuilder stringbuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
    	boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/merch/shoppingcart/shoppinglist.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/merch/shoppingcart/shoppinglist.js");
        }
        
        IData param = this.getPage().getData();
        if (StringUtils.isBlank(param.getString("USER_ID")))
        {
        	return;
        }
        
        String serviceName = this.getRenderCallSvc();
        if(StringUtils.isBlank(serviceName)){
        	serviceName = "SS.MerchShoppingCartSVC.loadShoppingCartInfo";
        }
        
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        IData rst = CSViewCall.callone(this, serviceName, param);
        this.getPage().setAjax(rst);
    }
}
