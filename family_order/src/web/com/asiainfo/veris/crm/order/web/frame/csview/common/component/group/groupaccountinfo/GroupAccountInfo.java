
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupaccountinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.infoshow.InfoShowTempComponent;

public abstract class GroupAccountInfo extends InfoShowTempComponent
{

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
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/groupaccountinfo/groupaccountinfo.js");
        super.renderComponent(writer, cycle);
    }
}
