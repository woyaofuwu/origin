
package com.asiainfo.veris.crm.order.web.group.param.ttrh.tt400;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class UserParamInfo extends IProductParamDynamic
{
    // 用户参数页面
    @Override
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData cond = new DataMap();
        cond.put("METHOD_NAME", "ChgUs");
        this.setCondition(cond);
        result.put("PARAM_INFO", cond);
        return result;
    }

    // 用户参数页面
    @Override
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData cond = new DataMap();
        cond.put("METHOD_NAME", "CrtUs");
        this.setCondition(cond);
        result.put("PARAM_INFO", cond);
        return result;
    }

    private void setCondition(IData condition)
    {

    }
}
