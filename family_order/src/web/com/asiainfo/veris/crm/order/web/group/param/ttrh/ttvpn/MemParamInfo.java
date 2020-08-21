
package com.asiainfo.veris.crm.order.web.group.param.ttrh.ttvpn;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class MemParamInfo extends IProductParamDynamic
{
    // 用户参数页面
    @Override
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData cond = new DataMap();
        cond.put("METHOD_NAME", "ChgMb");
        this.setCondition(cond);
        result.put("PARAM_INFO", cond);
        return result;
    }

    // 用户参数页面
    @Override
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData cond = new DataMap();
        cond.put("METHOD_NAME", "CrtMb");
        this.setCondition(cond);
        result.put("PARAM_INFO", cond);
        return result;
    }

    private void setCondition(IData cond)
    {

    }
}
