
package com.asiainfo.veris.crm.order.web.person.activeconsum;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ActiveConsumQry extends PersonBasePage
{
     
    /**
	 * REQ201608100026 新增套餐推荐界面开发需求
     * chenxy 20161012 
     * SS.ActiveConsumSVC.qryConsumInfos
	 * */
    public void qryConsumInfos(IRequestCycle cycle) throws Exception
    {
    	IData ajaxData=new DataMap();
    	IData data = getData("cond", true); 
        Pagination page = getPagination("recordNav");  
        IData params = new DataMap();
        params.put("X_GETMODE", 0);
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        params.put("TRADE_TYPE_CODE", "60");
        IDataset ucaInfos = CSViewCall.call(this, "CS.GetInfosSVC.getUCAInfos", params);
        if(ucaInfos!=null && ucaInfos.size()>0){
        	String userId=ucaInfos.getData(0).getData("USER_INFO").getString("USER_ID");
        	data.put("USER_ID", userId);
        	//取用户三个月话费信息
            IDataOutput result = CSViewCall.callPage(this, "SS.ActiveConsumSVC.qryConsumInfos", data, page); 
            //取用户三个月话费信息
            long dataCount=result.getDataCount();
            if(IDataUtil.isNotEmpty(result.getData())){
	            setRecordCount(dataCount);
	            //取用户当前及已推荐套餐信息
	            IDataset result2 = CSViewCall.call(this, "SS.ActiveConsumSVC.getUserProductInfo", data); 
	            if(IDataUtil.isNotEmpty(result2)){
	            	setEdit(result2.getData(0));
	            }
	            
	            setCond(data);
	            setInfos(result.getData());
	            ajaxData.put("USER_ID", userId);
	            ajaxData.put("EXISE", "TRUE");
            }else{
            	ajaxData.put("EXISE", "FALSE");
            }
        }else{
        	ajaxData.put("EXISE", "FALSE");
        } 
        setAjax(ajaxData);
    }
    /**
	 * REQ201608100026 新增套餐推荐界面开发需求
     * chenxy 20161012 
     * 计算推荐套餐
	 * */
    public void consum(IRequestCycle cycle) throws Exception
    {
    	IData data = getData(); 
    	data.put("CALL_TYPE", "1");
    	//重新计算套餐
    	IDataset results = CSViewCall.call(this, "SS.ActiveConsumSVC.consum", data); 
    	if(IDataUtil.isNotEmpty(results)){
    		setAjax(results.first());
    		data.put("PRODUCT_NEW",results.first().getString("PRODUCT_NEW",""));
    		setEdit(data);
        }
    }
    
    /**
     * REQ201611250012 套餐推荐界面补充需求（增加描述、时间等）
     * chenxy 20161216
     * 推荐套餐下发短信
     * */
    public void sendConsumSMS(IRequestCycle cycle) throws Exception
    {
        IData data = getData(); 
        data.put("CALL_TYPE", "1");
        // 推荐套餐下发短信
        IDataset results = CSViewCall.call(this, "SS.ActiveConsumSVC.sendConsumSMS", data); 
        if(IDataUtil.isNotEmpty(results)){
            setAjax(results.first()); 
        }
    }
    
    public abstract void setCond(IData info);
    public abstract void setInfo(IData info); 
    public abstract void setInfos(IDataset infos); 
    public abstract void setRecordCount(long recordCount);  
    public abstract void setEdit(IData info);
}
