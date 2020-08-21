
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.popuppagegrp;

import java.util.Iterator;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class PopupPageGrp extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setFrameName(null);
            setFrameListener(null);
            setFrameParams(null);
            setHidden(null);
            setPupupTitle(null);
        }
    }

    private String getFrameParamStr(IData inpara)
    {
        String paramS = "";
        inpara.remove("FRAME_NAME");
        inpara.remove("FRAME_LISTENER");
        inpara.remove("HIDDEN");
        inpara.remove("POPUP_TITLE");

        Iterator iterator = inpara.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            String value = inpara.getString(key, "");
            paramS = paramS + "&" + key + "=" + value;

        }
        return paramS;

    }

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/popuppagegrp/PopupPageGrp.js");
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");

        IData inpara = this.getPage().getData();
        String isRender = "";
        if (IDataUtil.isNotEmpty(inpara))
        {
            isRender = inpara.getString("IS_RENDER", "");
        }

        if (isRender.equals("true"))
        {
            String frameName = inpara.getString("FRAME_NAME");
            String frameListener = inpara.getString("FRAME_LISTENER");
            String hidden = inpara.getString("HIDDEN");
            String pupupTitle = inpara.getString("POPUP_TITLE");
            String frameParam = getFrameParamStr(inpara);

            setFrameListener(frameListener);
            setFrameName(frameName);
            setFrameParams(frameParam);
            setHidden(hidden);
            setPupupTitle(pupupTitle);
        }

    }

    public abstract void setFrameListener(String frameListener);

    public abstract void setFrameName(String frameName);

    public abstract void setFrameParams(String frameParams);

    public abstract void setHidden(String hidden);

    public abstract void setPupupTitle(String pupupTitle);

}
