
package com.asiainfo.veris.crm.order.web.group.param.gfff;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class MemParamInfoLimitaion extends IProductParamDynamic
{
	/**
     * 成员新增初始化
     * @date:2017-08-30
     * @author chenzg
     */
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData info = result.getData("PARAM_INFO");
        info.put("pam_NOTIN_PAY_END_DATE", SysDateMgr.getLastDateThisMonth4WEB());	//默认到当月底
        return result;
    }
    /**
     * 成员变更初始化
     * 
     * @author sht
     */
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        return result;
    }
    
}
