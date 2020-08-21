
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupuserinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.infoshow.InfoShowTempComponent;

public abstract class GroupUserInfo extends InfoShowTempComponent
{
    public abstract IData getGroupUserInfo();

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
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/groupuserinfo/groupuserinfo.js");
        super.renderComponent(writer, cycle);
    }

    public abstract void setGroupUserInfo(IData groupUserInfo);
}
