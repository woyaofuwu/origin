
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.authforgroup;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class AuthForGroup extends BaseTempComponent
{
    /**
     * render component
     * 
     * @param writer
     * @param cycle
     */
    @Override
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/groupauth/AuthForGroup.js");
        super.renderComponent(writer, cycle);
    }

}
