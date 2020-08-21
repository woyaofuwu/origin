
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.product.invoice;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: Invoice.java
 * @Description: 发票组件
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 4, 2014 10:34:41 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 4, 2014 maoke v1.0.0 修改原因
 */
public abstract class Invoice extends CSBizTempComponent
{
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);

        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/product/invoice/invoice.js");
        }
        else
        {
            this.getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/product/invoice/invoice.js");
        }

        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("Invoice.init();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
    }
}
