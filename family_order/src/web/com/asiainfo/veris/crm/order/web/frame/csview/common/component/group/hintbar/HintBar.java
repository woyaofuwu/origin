
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.hintbar;

import org.apache.tapestry.IBinding;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.web.BaseTempComponent;

public class HintBar extends BaseTempComponent
{

    private String hintInfo;

    private String errorInfo;

    private IBinding hintInfoBinding;

    private IBinding errorInfoBinding;

    public String getErrorInfo()
    {
        return errorInfo;
    }

    public IBinding getErrorInfoBinding()
    {
        return errorInfoBinding;
    }

    public String getHintInfo()
    {
        return hintInfo;
    }

    public IBinding getHintInfoBinding()
    {
        return hintInfoBinding;
    }

    public String getMessage()
    {
        if (isHasErrorInfo())
            return errorInfo;
        if (isHasHintInfo())
            return hintInfo;
        else
            return "";
    }

    public boolean isHasErrorInfo()
    {
        return errorInfo != null && errorInfo.trim().length() != 0;
    }

    public boolean isHasHintInfo()
    {
        return hintInfo != null && hintInfo.trim().length() != 0;
    }

    public boolean isHasInfo()
    {
        if (isHasErrorInfo())
            return true;
        return isHasHintInfo();
    }

    public boolean isShowErrorImage()
    {
        return isHasErrorInfo();
    }

    public boolean isShowHintImage()
    {
        return !isHasErrorInfo() && isHasHintInfo();
    }

    public void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        if (cycle.isRewinding())
        {
            return;
        }
        if (hintInfoBinding != null)
        {
            hintInfo = (String) hintInfoBinding.getObject("hintInfo", java.lang.String.class);
        }
        if (errorInfoBinding != null)
        {
            errorInfo = (String) errorInfoBinding.getObject("errorInfo", java.lang.String.class);
        }

        super.renderComponent(writer, cycle);

    }

    public void setErrorInfo(String errorInfo)
    {
        this.errorInfo = errorInfo;
    }

    public void setErrorInfoBinding(IBinding errorInfoBinding)
    {
        this.errorInfoBinding = errorInfoBinding;
    }

    public void setHintInfo(String hintInfo)
    {
        this.hintInfo = hintInfo;
    }

    public void setHintInfoBinding(IBinding hintInfoBinding)
    {
        this.hintInfoBinding = hintInfoBinding;
    }

}
