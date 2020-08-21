
package com.asiainfo.veris.crm.order.web.group.param.swbusim;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class UserParamInfo extends IProductParamDynamic
{
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {

        IData result = super.initChgUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");
        IData inparam = new DataMap();
        inparam.put("USER_ID", data.getString("USER_ID", ""));
        inparam.put("REMOVE_TAG", "0");
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset dataset = CSViewCall.call(bp, "CS.UserInfoQrySVC.getGrpUserInfoByUserId", inparam);
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData userInfo = dataset.getData(0);
            parainfo.put("DETMANAGERPHONE", userInfo.getString("RSRV_STR8", ""));
            parainfo.put("DETMANAGERINFO", userInfo.getString("RSRV_STR7", ""));
            parainfo.put("DETADDRESS", userInfo.getString("RSRV_STR9", ""));

        }
        result.put("PARAM_INFO", parainfo);
        return result;
    }

}
