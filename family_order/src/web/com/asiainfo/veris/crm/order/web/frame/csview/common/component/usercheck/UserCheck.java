
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.usercheck;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class UserCheck extends CSBizTempComponent
{

    private String eparchyCode;

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
    }

    public abstract String getBeforeAction();
    
    public abstract boolean getBindEvent();

    // 认证方式，默认为客户证件和服务密码
    public abstract String getCheckTag();

    public abstract String getClassHandler();

    // 认证号码中文描述，弹出错误时候使用
    public abstract String getDesc();

    public String getEparchyCode()
    {
        return this.eparchyCode;
    }

    public abstract String getExceptAction();

    // 认证校验服务号码ID
    public abstract String getFieldName();

    // 是否需要进行认证方式校验
    public abstract String getIsAuth();

    // 是否支持异地号码
    public abstract String getIsLocal();

    // 自定义回调事件
    public abstract String getTradeAction();
    
    // 自定义回调事件
    public abstract boolean getCacheSn();

    public void renderComponent(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        setEparchyCode(getTradeEparchyCode());
        /**
         * 组件初始化载入认证的脚本
         */
        getPage().addResAfterBodyBegin("scripts/csserv/component/usercheck/UserCheck.js");
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("$.userCheck.init('" + getFieldName() + "');\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
    }

    public abstract void setBeforeAction(String beforeAction);
    
    public abstract void setBindEvent(boolean bindEvent);
    
    public abstract void setCacheSn(boolean cacheSn);

    public abstract void setCheckTag(String checkTag);

    public abstract void setClassHandler(String className);

    public abstract void setDesc(String desc);

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public abstract void setExceptAction(String exceptAction);

    public abstract void setFieldName(String fieldName);

    public abstract void setIsAuth(String isAuth);

    public abstract void setIsLocal(String isLocal);

    public abstract void setTradeAction(String tradeAction);
}
