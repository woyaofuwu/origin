
package com.asiainfo.veris.crm.order.web.person.interboss.openchkacctrecqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class openChkAcctRecQry extends PersonBasePage
{
     
    /**
     * 能力开放平台日对账查询
     * SS.openChkAcctRecQrySVC.qryOpenChkAcctRecList
	 * */
    public void qryOpenChkAcctRecList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData("cond", true); 
        Pagination page = getPagination("recordNav");  
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        //取能力开放平台日对账查询数据 
        IDataOutput result = CSViewCall.callPage(this, "SS.openChkAcctRecQrySVC.qryOpenChkAcctRecList", data, page); 
        //回传能力开放平台日对账查询
        long dataCount=result.getDataCount();
        setRecordCount(dataCount);
         
   
         
        setCond(data);
        setInfos(result.getData());

        
        
    }
    
    /**
     * 能力开放平台日对账查询（日汇总信息）
     * SS.openChkAcctRecQrySVC.qryOpenChkAcctRecDayAll
	 * */
    public void qryOpenChkAcctRecDayAll(IRequestCycle cycle) throws Exception
    {
    	IData data = getData("cond", true); 
        Pagination page = getPagination("recordNav");  
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        
        IData result= new DataMap(); 
        
        //取能力开放平台日对账查询数据,获取BOSS多的数据
        data.put("RESULT_TYPE", "1");
        IDataOutput resultMoreData = CSViewCall.callPage(this, "SS.openChkAcctRecQrySVC.qryOpenChkAcctRecDayAll", data, page); 
        result.put("BOSS_MORE_NUMB", resultMoreData.getData().getData(0).getString("COUNT_NUMB"));
        result.put("BOSS_MORE_AMOUNT_PAY", resultMoreData.getData().getData(0).getString("COUNT_AMOUNT_PAY"));
        
        //取能力开放平台日对账查询数据,获取BOSS少的数据
        data.put("RESULT_TYPE", "2");
        IDataOutput resultLessData = CSViewCall.callPage(this, "SS.openChkAcctRecQrySVC.qryOpenChkAcctRecDayAll", data, page); 
        result.put("BOSS_LESS_NUMB", resultLessData.getData().getData(0).getString("COUNT_NUMB"));
        result.put("BOSS_LESS_AMOUNT_PAY", resultLessData.getData().getData(0).getString("COUNT_AMOUNT_PAY"));
         
       // IDataset results= new DatasetList();
        //results.add(result);
        setCond(data);
        setCommInfo(result);

        
        
    }
    
    public abstract void setCond(IData info);
    public abstract void setInfo(IData info);
    

    public abstract void setInfos(IDataset infos);
    public abstract void setCommInfo(IData commInfo);

    public abstract void setRecordCount(long recordCount);   
}
