
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.exitgrpmemberbox;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class ExitGrpMemberBox extends BaseTempComponent
{
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/exitgrpmemberbox/ExitGrpMemberBox.js");
        super.renderComponent(writer, cycle);
    }
}
