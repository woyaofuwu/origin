
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.popupgrpuserlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public class PopupGrpUserList extends BaseTempComponent
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
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/popupgrpuserlist/PopupGrpUserList.js");
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");
        super.renderComponent(writer, cycle);
    }

}
