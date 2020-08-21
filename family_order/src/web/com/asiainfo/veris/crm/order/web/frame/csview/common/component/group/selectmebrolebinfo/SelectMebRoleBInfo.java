
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmebrolebinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class SelectMebRoleBInfo extends CSBizTempComponent
{

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/selectmebrolebinfo/SelectMebRoleBInfo.js");

    }

}
