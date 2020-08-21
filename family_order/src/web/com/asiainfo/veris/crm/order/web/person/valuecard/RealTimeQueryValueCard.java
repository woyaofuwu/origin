package com.asiainfo.veris.crm.order.web.person.valuecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
/**
 * 自办营业员有价卡销售信息查询
 * @author liutt
 *
 */
public abstract class RealTimeQueryValueCard extends PersonBasePage{

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setListInfos(IDataset listInfos);

    public abstract void setPageCount(long count);
    
    public void queryValueCardInfo(IRequestCycle cycle) throws Exception
    {
    	 Pagination page = getPagination("recordNav");
         IData data = getData("cond", true);
         data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());       
         IDataOutput result = CSViewCall.callPage(this, "SS.RealTimeQueryValueCardSVC.queryValueCardInfo", data, page);
         setListInfos(result.getData());
         setCondition(data);
         setPageCount(result.getDataCount());
         setAjax("DATA_COUNT", "" + result.getData().size());
    }
}
