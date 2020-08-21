
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.infoshow;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.BaseTempComponent;

public abstract class InfoShowTempComponent extends BaseTempComponent
{
    public abstract String getTitleLev();

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     */
    @Override
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        String titlelev = getTitleLev();

        if (StringUtils.isBlank(titlelev))
        {
            titlelev = "1";
        }

        if (titlelev.equals("1"))
        {
            titlelev = "c_title";
        }
        else if (titlelev.equals("2"))
        {
            titlelev = "c_title c_title-2";
        }
        else if (titlelev.equals("3"))
        {
            titlelev = "c_title c_title-3";
        }
        else
        {
            titlelev = "c_title";
        }
        setTitleLev(titlelev);

        super.renderComponent(writer, cycle);
    }

    public abstract void setTitleLev(String titleLev);

}
