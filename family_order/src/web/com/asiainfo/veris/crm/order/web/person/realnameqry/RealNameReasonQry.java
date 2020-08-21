
package com.asiainfo.veris.crm.order.web.person.realnameqry;

import java.text.SimpleDateFormat; 
import java.util.Date;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class  RealNameReasonQry extends PersonBasePage
{
     
    /**
	 * REQ201611030020 关于展示客户非实名原因的需求
     * chenxy 20161125
	 * */
    public void queryRealNameReasonList(IRequestCycle cycle) throws Exception
    {
    	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date nowDate=new Date();
		String thisTime=df.format(nowDate);
    	IData ajaxData=new DataMap();
    	IData data = getData("cond", true); 
    	data.put("STAT_DATE",thisTime);
        Pagination page = getPagination("recordNav");  
        IData params = new DataMap();
        params.put("X_GETMODE", 0);
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        params.put("TRADE_TYPE_CODE", "60");
        IDataset ucaInfos = CSViewCall.call(this, "CS.GetInfosSVC.getUCAInfos", params);
        if(ucaInfos!=null && ucaInfos.size()>0){
        	String userId=ucaInfos.getData(0).getData("USER_INFO").getString("USER_ID");
        	String isRealName=ucaInfos.getData(0).getData("CUST_INFO").getString("IS_REAL_NAME","");
        	String brandCode=ucaInfos.getData(0).getData("USER_INFO").getString("BRAND_CODE");
        	ajaxData.put("USER_EXISE", "TRUE");
        	//如果是非实名制，则查询列表
        	if(!"1".equals(isRealName)){
        		data.put("USER_ID", userId);
            	//取用户非实名原因
        		IDataset results = CSViewCall.call(this, "SS.RealNameReasonSVC.qryUserNonRealNameReason", data, page); 
                 
                if(IDataUtil.isNotEmpty(results)){ 
    	            //取用户当前及已推荐套餐信息
    	           
    	            setCond(data);
    	             
    	            IData data1=results.getData(0);
    	            data.put("IS_REAL_NAME", "非实名");
    	            data.putAll(data1);
    	            setShowInfo(data);
    	            results.remove(0);
    	            setInfos(results); 
                }else{ 
                	ajaxData.put("IS_REAL_NAME", "FALSE");
                	ajaxData.put("REALNAME_LIST", "NOT");
                }
        	}else{
        		ajaxData.put("IS_REAL_NAME", "TRUE");
        		data.put("IS_REAL_NAME", "是"); 
        		data.put("CMCC_BRAND_CODE",brandCode);
        		setShowInfo(data);
        	}
        	
        }else{
        	ajaxData.put("USER_EXISE", "FALSE");
        } 
        setAjax(ajaxData);
    } 
    
    public abstract void setCond(IData info);
    public abstract void setInfo(IData info); 
    public abstract void setInfos(IDataset infos); 
    public abstract void setRecordCount(long recordCount);   
    public abstract void setShowInfo(IData data); 
}
