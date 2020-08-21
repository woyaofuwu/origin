
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.postinfofield;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.baseinfofield.BaseInfoField;

public abstract class PostInfoField extends BaseInfoField
{
    private IData item;

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        setPostInfo(null);
        setItem(null);
        super.cleanupAfterRender(cycle);
    }

    public IData getItem()
    {
        return this.item;
    }

    public abstract IData getPostInfo();

    public void renderComponent(StringBuilder stringBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        /**
         * 组件初始化载入认证的脚本
         */
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/CommLib.js");
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/baseinfofield/BaseInfoField.js");
        getPage().addResAfterBodyBegin("scripts/csserv/component/person/postinfofield/PostInfoField.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("$.postInfo.init();\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
    }

    public void setItem(IData item)
    {
        this.item = item;
    }

    public abstract void setPostInfo(IData postInfo);
}
