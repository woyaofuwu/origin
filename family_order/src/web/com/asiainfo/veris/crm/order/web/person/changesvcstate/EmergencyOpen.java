
package com.asiainfo.veris.crm.order.web.person.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: EmergencyOpen.java
 * @Description: 紧急开机
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-14 下午2:47:24
 */

public abstract class EmergencyOpen extends ChangeSvcState
{
    public void onTradeInit(IRequestCycle requestCycle) throws Exception
    {
        IData pageDate = getData();
        IData param = new DataMap();
        param.put("USER_ID", pageDate.getString("USER_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, pageDate.getString("EPARCHY_CODE"));
        IDataset creditDataset = CSViewCall.call(this, "SS.ChangeSvcStateSVC.queryUserEmergencyInfo", param);
        setCreditInfo(creditDataset.getData(0));
    }

    public abstract void setCreditInfo(IData data);
}
