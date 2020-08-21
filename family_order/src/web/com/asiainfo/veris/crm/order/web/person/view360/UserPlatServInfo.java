package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserPlatServInfo extends PersonBasePage
{

    /**
     * 客户资料综合查询 - 平台业务信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryPlatServInfo(IRequestCycle cycle) throws Exception
    {
	
        // 获取页面信息
        IData params = getData();
      
        params.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        if ("1".equals(params.getString("SelectTag", "0")))//查询所有记录
        {  
            IDataset reDataSet = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs12", params);                                                   
            setInfos(reDataSet);
        }
        else//查询状态为A正常、N暂停状态的记录
		{
	        IDataset reDataSet = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs11", params);           
		    setInfos(reDataSet);
		}
	    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
