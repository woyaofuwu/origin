
package com.asiainfo.veris.crm.order.web.group.param.ecard;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class UserParamInfo extends IProductParamDynamic
{

    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        String userId = data.getString("USER_ID");
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        IDataset userInfo = CSViewCall.call(bp, "CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = (IData) userInfo.get(0);

            parainfo.put("APN_TYPE", userData.get("RSRV_STR4"));
            parainfo.put("MGR_NAME", userData.get("RSRV_STR6"));
            parainfo.put("MGR_PHONE", userData.get("RSRV_STR7"));
            parainfo.put("DETADDRESS", userData.get("RSRV_STR8"));
            parainfo.put("APN_NAME", userData.get("RSRV_STR9"));
            parainfo.put("APN_REMARK", userData.get("REMARK"));
        }

        parainfo.put("NOTIN_METHOD_NAME", "ChgUs");
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        parainfo.put("NOTIN_METHOD_NAME", "CrtUs");
        return result;
    }

}
