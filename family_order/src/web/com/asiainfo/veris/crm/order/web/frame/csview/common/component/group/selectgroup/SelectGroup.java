
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroup;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class SelectGroup extends BaseTempComponent
{

    private final static String SCRIPT = "scripts/csserv/component/group/selectgroup/SelectGroup.js";

    private final static String TIPSCRIPT = "scripts/csserv/component/businesstip/businesstip.js";

    public abstract String getAfterAction();

    public abstract String getEcName();

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

        // Body body = Body.get(cycle);
        cycle.getPage().addResAfterBodyBegin(SCRIPT);
        cycle.getPage().addResAfterBodyBegin(TIPSCRIPT);

        super.renderComponent(writer, cycle);

    }

    public abstract void setAfterAction(String afterAction);

    public abstract void setEcName(String ecName);

}
