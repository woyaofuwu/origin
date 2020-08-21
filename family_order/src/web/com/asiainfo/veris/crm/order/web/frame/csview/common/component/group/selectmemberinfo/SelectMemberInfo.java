
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmemberinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public class SelectMemberInfo extends CSBizTempComponent
{

    public String getPageName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/selectmemberinfo/SelectMemberInfo.js");
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");
        // super.renderComponent(writer, cycle);

    }

}
