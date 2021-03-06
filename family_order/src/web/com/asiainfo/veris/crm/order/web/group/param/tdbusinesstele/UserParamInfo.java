
package com.asiainfo.veris.crm.order.web.group.param.tdbusinesstele;

/* $Id: UserParamInfo.java,v 1.1 2011/09/14 07:32:59 lixiuyu Exp $ */

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");

        // 绑定管理员信息
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, data.getString("USER_ID", ""));
        parainfo.put("CUST_MANAGER", userInfo.getString("RSRV_STR5", ""));
        parainfo.put("DETMANAGERPHONE", userInfo.getString("RSRV_STR8", ""));
        parainfo.put("DETMANAGERINFO", userInfo.getString("RSRV_STR7", ""));
        parainfo.put("DETADDRESS", userInfo.getString("RSRV_STR9", ""));

        result.put("PARAM_INFO", parainfo);
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");

        parainfo.put("METHOD_NAME", "CrtUs");
        result.put("PARAM_INFO", parainfo);
        return result;
    }

}
