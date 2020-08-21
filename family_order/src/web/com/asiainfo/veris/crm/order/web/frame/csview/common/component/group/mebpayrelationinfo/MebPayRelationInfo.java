
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.mebpayrelationinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.web.BaseTempComponent;

public abstract class MebPayRelationInfo extends BaseTempComponent
{
    public abstract IData getPayPlanInfo();

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

        super.renderComponent(writer, cycle);
    }

    public abstract void setPayPlanInfo(IData groupUserInfo);
}
