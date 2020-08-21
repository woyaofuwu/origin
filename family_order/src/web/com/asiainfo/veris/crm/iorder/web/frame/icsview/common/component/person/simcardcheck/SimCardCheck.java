
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.person.simcardcheck;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

public abstract class SimCardCheck extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    public abstract String getAfterAction();

    public abstract String getBeforeAction();

    public abstract String getExceptAction();

    public abstract String getFieldId();

    public abstract String getSerialNumber();

    public abstract String getTradeAction();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/simcardcheck/SimCardCheck.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("$.simcardcheck.init('" + getFieldId() + "');\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");

    }

    public abstract void setAfterAction(String afterAction);

    public abstract void setBeforeAction(String beforeAction);

    public abstract void setExceptAction(String exceptAction);

    public abstract void setFieldId(String fieldId);

    public abstract void setSerialNumber(String serialNumber);

    public abstract void setTradeAction(String tradeAction);

}
