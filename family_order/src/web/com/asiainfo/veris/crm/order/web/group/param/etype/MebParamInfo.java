
package com.asiainfo.veris.crm.order.web.group.param.etype;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class MebParamInfo extends IProductParamDynamic
{
    private static transient Logger logger = Logger.getLogger(MebParamInfo.class);

    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");
        parainfo.put("NOTIN_METHOD_NAME", "ChgMb");

        IData userInParam = new DataMap();
        userInParam.put("USER_ID", data.getString("MEB_USER_ID"));
        userInParam.put("RSRV_VALUE_CODE", "VGPR");
        userInParam.put("USER_ID_A", data.getString("GRP_USER_ID"));

        IDataset userInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", userInParam);
        // 判断OTHER表中有没有数据，没有从ESOP获取
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = (IData) userInfo.get(0);
            parainfo.put("USE_APN", userData.get("RSRV_VALUE"));
            parainfo.put("IP_ADDRESS", userData.get("RSRV_STR1"));

        }

        return result;
    }

    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        parainfo.put("NOTIN_METHOD_NAME", "CrtMb");

        return result;
    }

}
