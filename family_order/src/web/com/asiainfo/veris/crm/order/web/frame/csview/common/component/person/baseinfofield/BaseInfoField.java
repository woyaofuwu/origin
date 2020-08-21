
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.baseinfofield;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class BaseInfoField extends CSBizTempComponent
{
    public abstract boolean getIsHidden();

    public abstract String getRequiredField();

    public abstract void setIsHidden(boolean isHidden);

    public abstract void setRequiredField(String requiredField);
}
