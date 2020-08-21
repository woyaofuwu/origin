
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.grpuserlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class GrpUserList extends BaseTempComponent
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

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/grpuserlist/GrpUserList.js");
        super.renderComponent(writer, cycle);

    }

}
