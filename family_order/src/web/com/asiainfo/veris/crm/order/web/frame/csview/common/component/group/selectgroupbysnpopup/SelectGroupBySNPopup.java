
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroupbysnpopup;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class SelectGroupBySNPopup extends BaseTempComponent
{

    private final static String SCRIPT = "scripts/csserv/component/group/selectgroupbysnpopup/selectgroupbysnpopup.js";

    private final static String TIPSCRIPT = "scripts/csserv/component/businesstip/businesstip.js";

    public abstract String getAfterAction();

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
        cycle.getPage().addResAfterBodyBegin(TIPSCRIPT);
        super.renderComponent(writer, cycle);

    }

    public abstract void setAfterAction(String afterAction);

}
