
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.productctrlinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public class ProductCtrlInfo extends BaseTempComponent
{

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/product/productctrlinfo/ProductCtrlInfo.js");
        super.renderComponent(writer, cycle);
    }

}
