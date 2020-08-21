
package com.asiainfo.veris.crm.order.web.person.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: VipAssureOpen.java
 * @Description: 大客户担保开机
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-24 上午11:12:46
 */
public abstract class VipAssureOpen extends ChangeSvcState
{

    public void queryVipAssureSnInfo(IRequestCycle requestCycle) throws Exception
    {
        IData pageData = getData();
        pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("EPARCHY_CODE"));
        IDataset vipAssureSnInfos = CSViewCall.call(this, "SS.ChangeSvcStateSVC.queryVipAssureSnInfo", pageData);
        if (IDataUtil.isNotEmpty(vipAssureSnInfos))
        {
            IData tempData = vipAssureSnInfos.getData(0);
            setBusiInfo(tempData.getData("BUSI_INFO"));
            setAssuerTimesList(tempData.getDataset("OPEN_HOURS"));
        }
    }

    public abstract void setAssuerTimesList(IDataset datasIDataset);

    public abstract void setBusiInfo(IData data);
}
