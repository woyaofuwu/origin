
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupmemberinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.infoshow.InfoShowTempComponent;

public abstract class GroupMemberInfo extends InfoShowTempComponent
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

        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }

        super.renderComponent(writer, cycle);
    }
}
