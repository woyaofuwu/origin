
package com.asiainfo.veris.crm.order.web.person.sundryquery.monitorinfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryRedMember extends PersonBasePage
{

    /**
     * 不良信息号码用户资料查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryRedMemberInfos(IRequestCycle cycle) throws Exception
    {
        Pagination page = getPagination("pageNav");
        IData inputData = this.getData();
        
        String queryType=inputData.getString("QUERY_TYPE");
        if(queryType.equals("2"))//1为按号码查询,2为按时间段查询
        {
        	String start_time = inputData.getString("START_TIME", "");
        	String end_time = inputData.getString("END_TIME", "");
        	
        	if(!checkdate(start_time))
        	{
        		CSViewException.apperr(CrmCommException.CRM_COMM_103, "请输入正确的时间！");
        		return ;
        	}
        	
        	if(!checkdate(end_time))
        	{
        		CSViewException.apperr(CrmCommException.CRM_COMM_103, "请输入正确的时间！");
        		return ;
        	}
        }
        
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataOutput result = CSViewCall.callPage(this, "SS.QueryRedMemberSVC.queryRedMemberInfos", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(inputData);
        setPageCount(result.getDataCount());
    }
    
    
    public boolean checkdate(String datetime){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
           try{
                   Date date = formatter.parse(datetime);
                   return   datetime.equals(formatter.format(date));
           }catch(Exception   e){
                 return   false;
           }
       }

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

}
