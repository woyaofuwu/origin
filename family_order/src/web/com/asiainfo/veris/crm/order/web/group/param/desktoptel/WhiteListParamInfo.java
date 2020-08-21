
package com.asiainfo.veris.crm.order.web.group.param.desktoptel;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;

public abstract class WhiteListParamInfo extends GroupParamPage
{

    public abstract void setInfo(IData info);

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    /**
     * @description 初始化 集团成员产品变更 参数页面
     * @author yish
     */
    public void initSvcParamsInfo(IRequestCycle cycle) throws Throwable
    {
        IData data = getData();
        setCondition(data);
    }

    /**
     * 作用：刷新
     */
    public void refreshBWLish(IRequestCycle cycle) throws Throwable
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("EC_USER_ID", data.getString("MEB_USER_ID"));
        param.put("SERVICE_ID", data.getString("SERVICE_ID", ""));
        param.put("USER_TYPE_CODE", "IB");
        param.put(Route.ROUTE_EPARCHY_CODE, data.getString("MEB_EPARCHY_CODE"));

        IDataset bwList = CSViewCall.call(this, "CS.UserBlackWhiteInfoQrySVC.getBlackWhitedataByGSS", param);
        setInfos(bwList);
    }
}
