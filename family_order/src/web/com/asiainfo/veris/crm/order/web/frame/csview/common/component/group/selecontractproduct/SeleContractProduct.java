
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selecontractproduct;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class SeleContractProduct extends BaseTempComponent
{

    private final static String SCRIPT = "scripts/csserv/component/group/selecontractproduct/SeleContractProduct.js";

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     */
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;

        cycle.getPage().addResAfterBodyBegin(SCRIPT);

        super.renderComponent(writer, cycle);

    }

}
