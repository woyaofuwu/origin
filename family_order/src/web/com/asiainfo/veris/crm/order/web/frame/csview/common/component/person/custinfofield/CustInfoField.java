
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.custinfofield;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.baseinfofield.BaseInfoField;

public abstract class CustInfoField extends BaseInfoField
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        setCustInfo(null);
        super.cleanupAfterRender(cycle);
    }

    public abstract IData getCustInfo();

    public abstract String getEparchyCode();

    public abstract boolean getIsRealName();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        /**
         * 组件初始化载入认证的脚本
         */
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/CommLib.js");
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/eform/CommonEForm.js");
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/baseinfofield/BaseInfoField.js");
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/custinfofield/CustInfoField.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("$.custInfo.init();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
        if (StringUtils.isBlank(getEparchyCode()))
        {
            setEparchyCode(getTradeEparchyCode());
        }
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setEparchyCode(String eparchyCode);

    public abstract void setIsRealName(boolean isRealName);
    
    public abstract void setBlackTradeType(String blackTradeType);
}
