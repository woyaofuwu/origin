
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupsubmit;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public class GroupSubmit extends CSBizTempComponent
{

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    @Override
    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/print/PrintMgr.js");
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/submit/GroupSubmit.js");
    }
}
