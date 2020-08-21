
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.person.custinfofield;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.baseinfofield.BaseInfoField;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

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
        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/CommLib.js");
        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/eform/CommonEForm.js");
        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/baseinfofield/BaseInfoField.js");
        getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/person/custinfofield/CustInfoField.js");
        // 过户(新)页面鉴权号码后$.custInfo.init()报错，将此方法放置CustInfoField.html末尾执行
        /*writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("$.custInfo.init();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");*/
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
