
package com.asiainfo.veris.crm.order.web.group.param.ghyt;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class MemParamInfo extends IProductParamDynamic
{

    /**
     * 成员变更初始化
     * 
     * @author sht
     */
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        return result;
    }
    
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        return result;
    }
}
