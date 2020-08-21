
package com.asiainfo.veris.crm.order.web.person.blackusermanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BlackUserQryLog extends PersonBasePage
{
     
    /**
	 * REQ201606300007 关于增加系统黑名单后台查询日志的需求
     * chenxy 20160706 
     * 黑名单用户日志查询
     * SS.BlackUserManageSVC.qryBlackUserLog
	 * */
    public void qryBlackUserLog(IRequestCycle cycle) throws Exception
    {
    	IData data = getData("cond", true); 
        Pagination page = getPagination("recordNav");  
        //取黑名单用户日志 
        IDataOutput result = CSViewCall.callPage(this, "SS.BlackUserManageSVC.qryBlackUserLog", data, page); 
        
        //回传黑名单用户日志
        long dataCount=result.getDataCount();
        setRecordCount(dataCount);
         
        setCond(data);
        setInfos(result.getData());
    }
    
    public abstract void setCond(IData info);
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount);   
}
