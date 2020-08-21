
package com.asiainfo.veris.crm.order.web.person.billsecprotectuserlistqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class billSecProtectUserListQry extends PersonBasePage
{
     
    /**
     * 计费安全保护用户清单查询
     * SS.BillSecProtectUserListQrySVC.qryBillSecProtectUserList
	 * */
    public void qryBillSecProtectUserList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData("cond", true); 
        Pagination page = getPagination("recordNav");  
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        //取计费安全保护用户清单数据 
        IDataOutput result = CSViewCall.callPage(this, "SS.BillSecProtectUserListQrySVC.qryBillSecProtectUserList", data, page); 
        
        //回传计费安全保护用户清单
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
