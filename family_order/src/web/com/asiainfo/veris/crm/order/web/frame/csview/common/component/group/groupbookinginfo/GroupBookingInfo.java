
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupbookinginfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class GroupBookingInfo extends BaseTempComponent
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

        try
        {
            cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/groupbookinginfo/GroupBookingInfo.js");
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }

        super.renderComponent(writer, cycle);
    }
}
