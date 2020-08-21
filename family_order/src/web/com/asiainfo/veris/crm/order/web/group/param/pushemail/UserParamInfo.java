
package com.asiainfo.veris.crm.order.web.group.param.pushemail;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {

        IData result = super.initChgUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");

        String grpUserId = data.getString("USER_ID", "");

        // 调用后台服务，获取user信息
        IData userinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, grpUserId);

        if (IDataUtil.isNotEmpty(userinfo))
        {
            parainfo.put("MasID", userinfo.getString("RSRV_STR1"));
            parainfo.put("ManagerName", userinfo.getString("RSRV_STR6"));
            parainfo.put("ManagerPhone", userinfo.getString("RSRV_STR7"));
            parainfo.put("ManagerInfo", userinfo.getString("RSRV_STR8"));
        }

        result.put("PARAM_INFO", parainfo);

        return result;
    }
}
