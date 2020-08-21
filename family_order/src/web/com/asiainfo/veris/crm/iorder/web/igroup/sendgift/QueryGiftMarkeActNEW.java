package com.asiainfo.veris.crm.iorder.web.igroup.sendgift;

import org.apache.tapestry.IRequestCycle;
 
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryGiftMarkeActNEW extends PersonBasePage
{
    
	 public void initial(IRequestCycle cycle) throws Exception
	 {
		String expTag="0";
	    setExpTag(Integer.parseInt(expTag));
	 }
	
    /**
	 * 营销活动综合查询
	 * */
    public void queryInfoList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData("cond", true);
    	IData data2=getData();
    	String AGENT_DEPART_ID1=data2.getString("AGENT_DEPART_ID1","");
    	String endDate=data.getString("END_DATE","");
    	if(!"".equals(endDate)){
    		endDate=endDate+" 23:59:59";
    		data.put("END_DATE", endDate);
    	}
    	if(!"".equals(AGENT_DEPART_ID1)){
    		String departId=AGENT_DEPART_ID1.substring(0,5);
    		data.put("DEPART_ID", departId);
    	}
        Pagination page = getPagination("nav");  
        IDataOutput result = CSViewCall.callPage(this, "SS.GiftMarkeQrySVC.querySendGiftInfo", data, page); 
         
        long dataCount=result.getDataCount();
        setCount(dataCount);
         
        setCond(data);
        setInfos(result.getData());
    }
    
    public abstract void setCond(IData cond);
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setExpTag(int expTag);
    
    public abstract void setCount(long count);
}

