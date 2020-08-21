
package com.asiainfo.veris.crm.order.web.group.param.desktoptel;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;

public abstract class ConvenientNumberParamInfo extends GroupParamPage
{

    /**
     * @description 初始化 集团成员产品变更 参数页面
     * @author liuzz
     */
    public void initSvcParamsInfo(IRequestCycle cycle) throws Throwable
    {
        IData data = getData();
        setCondition(data);
    }

    /**
     * 作用：刷新缩位短号列表
     * 
     * @author liuzz
     * @exception throwable
     */
    public void refreshLishArea(IRequestCycle cycle) throws Throwable
    {
        IData data = getData();
        String eparchyCode = data.getString("MEB_EPARCHY_CODE", "");
        String userIdB = data.getString("MEB_USER_ID", "");
        IData param = new DataMap();
        param.put("USER_ID", userIdB);
        param.put("RSRV_VALUE_CODE", "SHORTDIALSN");
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset otherInfos = CSViewCall.call(this, "CS.UserOtherInfoQrySVC.getUserOtherByUseridRsrvcode", param);

        setInfos(otherInfos);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
