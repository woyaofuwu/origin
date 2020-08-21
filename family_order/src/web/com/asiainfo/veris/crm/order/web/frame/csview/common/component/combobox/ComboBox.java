
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.combobox;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public abstract class ComboBox extends BaseTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        setModel(null);
        setHeadField(null);
        setHeadText(null);
        setBoxWidth(null);
        setShowHeads(null);
        setValueField(null);
        setValue(null);
        setShowFields(null);
    }

    public abstract String getBoxWidth();

    public abstract String getClickAfterAction();

    public abstract String getHeadField();

    public abstract String getHeadText();

    public abstract Object getModel();

    public abstract String getShowFields();

    public abstract String getShowHeads();

    public abstract String getValue();

    public abstract String getValueField();

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
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/combobox/ComboBox.js");

        super.renderComponent(writer, cycle);
    }

    public abstract void setBoxWidth(String width);

    public abstract void setHeadField(String filed);

    public abstract void setHeadText(String HeadText);

    public abstract void setModel(Object object);

    public abstract void setShowFields(String fields);

    public abstract void setShowHeads(String value);

    public abstract void setValue(String value);

    public abstract void setValueField(String value);
}
