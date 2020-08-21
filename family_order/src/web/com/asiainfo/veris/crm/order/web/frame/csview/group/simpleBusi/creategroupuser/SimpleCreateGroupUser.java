
package com.asiainfo.veris.crm.order.web.frame.csview.group.simpleBusi.creategroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SimpleCreateGroupUser extends GroupBasePage
{

    public abstract IData getCondition();

    public abstract IData getInfo();

    public void initial(IRequestCycle cycle) throws Throwable
    {

        IData param = getData();
        IData condition = new DataMap();
        String grpUserEparchyCode = getTradeEparchyCode();
        condition.put("GRP_USER_EPARCHYCODE", grpUserEparchyCode);

        String ttGrp = param.getString("IS_TTGRP", "false");
        condition.put("IS_TTGRP", ttGrp);

        setCondition(condition);

    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setServiceName(String serviceName);
}
