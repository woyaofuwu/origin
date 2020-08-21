
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.writecard;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class WriteCard extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    public abstract String getAfterWriteSVC();

    public abstract String getBeforeWriteSVC();

    public abstract String getCheckWriteSVC();

    public abstract String getEparchyCode();

    public abstract String getIsRender();

    public abstract int getMode();

    public abstract String getReadAfterAction();

    public abstract String getReadBeforeAction();

    public abstract String getReadTradeAction();

    public abstract String getSerialNumber();

    public abstract String getSnbField();

    public abstract String getTradeTypeCode();

    public abstract String getWriteAfterAction();

    public abstract String getWriteBeforeAction();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        if (null == getEparchyCode() || "".equals(getEparchyCode()))
        {
            setEparchyCode(getTradeEparchyCode());
        }

        getPage().addResAfterBodyBegin("scripts/csserv/component/person/simcard/SimCard.js");
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/writecard/WriteCard.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("$.simcard.init();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");

    }

    public abstract void setAfterWriteSVC(String afterWriteSVC);

    public abstract void setBeforeWriteSVC(String beforeWriteSVC);

    public abstract void setCheckWriteSVC(String checkWriteSVC);

    public abstract void setEparchyCode(String eparchyCode);

    public abstract void setIsRender(String isRender);

    public abstract void setMode(int mode);

    public abstract void setReadAfterAction(String readAfterAction);

    public abstract void setReadBeforeAction(String readBeforeAction);

    public abstract void setReadTradeAction(String readTradeAction);

    public abstract void setSerialNumber(String serialNumber);

    public abstract void setSnbField(String snbField);

    public abstract void setTradeTypeCode(String tradeTypeCode);

    public abstract void setWriteAfterAction(String writeAfterAction);

    public abstract void setWriteBeforeAction(String writeBeforeAction);

}
