
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.person.assureinfofield;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.baseinfofield.BaseInfoField;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

public abstract class AssureInfoField extends BaseInfoField
{
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        setAssureInfo(null);
        super.cleanupAfterRender(cycle);
    }

    public abstract IData getAssureInfo();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/CommLib.js");
        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/baseinfofield/BaseInfoField.js");
        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/assureinfofield/AssureInfoField.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("$.assureInfo.init();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
    }

    public abstract void setAssureInfo(IData assureInfo);
}
