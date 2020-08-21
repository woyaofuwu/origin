
package com.asiainfo.veris.crm.order.web.person.sundryquery.userinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

/**
 * 用户资料模糊查询
 */
public abstract class QuerySnByUsrpid extends PersonQueryPage
{

    /**
     * 导出用户资料列表
     * 
     * @author chenhao 2009-4-2
     * @param cycle
     * @throws Exception
     */
    public void exportSnByUsrpid(IRequestCycle cycle) throws Exception
    {

        IData condPrams = getData("cond", true);

        // QueryTradeBean bean = new QueryTradeBean();
        // IDataset results = bean.querySnByUsrpid(condPrams, new Pagination(true));
        // ExcelParser.exportExcel(pd, "person/sundryquery/userinfo/QuerySnByUsrpidList.xml"
        // , "用户资料列表.xls"
        // , new IDataset[]{results});
    }

    /**
     * 用户资料模糊查询
     */
    public void querySnByUsrpid(IRequestCycle cycle) throws Exception
    {
        IData condPrams = getData("cond", true);
        condPrams.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataOutput results = CSViewCall.callPage(this, "SS.QuerySnByUsrpidSVC.querySnByUsrpid", condPrams, getPagination("navt"));
        if (results == null)
        {
            CSViewException.apperr(CrmUserException.CRM_USER_260);
        }
        setCount(results.getDataCount());
        setInfos(results.getData());
        setCond(getData("cond", true));
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);
    
    // 宽带业务
    public void qryTHCustomerContactInfo(IRequestCycle cycle) throws Exception
    {
    	
    }
    
    
    /**
     * 宽带业务查询
     */
    public void broadQuerySnByUsrpid(IRequestCycle cycle) throws Exception
    {
        IData condPrams = getData("broad", true);
        condPrams.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataOutput results = CSViewCall.callPage(this, "SS.QuerySnByUsrpidSVC.broadquerySnByUsrpid", condPrams, getPagination("navt"));
        if (results == null)
        {
            CSViewException.apperr(CrmUserException.CRM_USER_138);
        }
        for (int i = 0; i < results.getData().size(); i++) {
        	results.getData().getData(i).put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", 
        			results.getData().getData(i).getString("USER_STATE_CODESET", "")));
		}
        setCount(results.getDataCount());
        setInfos(results.getData());
        setCond(getData("broad", true));
        IData map = new DataMap();
        map.put("counts", results.getDataCount());
        setAjax(map);
    }
}
