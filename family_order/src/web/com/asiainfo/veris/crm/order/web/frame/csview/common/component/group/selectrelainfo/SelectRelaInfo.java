
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectrelainfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public class SelectRelaInfo extends BaseTempComponent
{

    // private final static String SCRIPT =
    // "/com/linkage/saleserv/core/components/group/selectrelainfo/SelectRelaInfo.script";
    //	
    // private final static String SCRIPT_ATTRIBUTE = SelectMemberInfo.class.getName();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;
        // Body body = Body.get(cycle);
        // body.addInitializationScript("checkResult();");
        //	    	
        // Util.renderScript(cycle,SCRIPT,SCRIPT_ATTRIBUTE);

        super.renderComponent(writer, cycle);

    }
}
