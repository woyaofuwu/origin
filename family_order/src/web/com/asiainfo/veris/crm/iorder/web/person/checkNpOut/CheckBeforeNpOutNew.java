
package com.asiainfo.veris.crm.iorder.web.person.checkNpOut;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CheckBeforeNpOutNew extends PersonBasePage
{
    public void init(IRequestCycle cycle) throws Exception
    {
      /*  if ((StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_SPECUSEROPENLIMIT")) || (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSERLIMITCOUNT")))
        {
            setPrivateFlag(true);
        }
        else
        {
            setPrivateFlag(false);
        }*/
    	
    	setPrivateFlag(true);
    }

    public void queryNpOutInfo(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData("cond", true);
        //Pagination page = getPagination("pageNav");
        inputData.put("DEPART_ID", this.getVisit().getDepartId());
        inputData.put("EPARCHY_CODE", getTradeEparchyCode());
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        inputData.put("IN_MODE_CODE", "0");
        

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.CheckBeforeNPOut.checkNpOut", inputData);
        /*IDataset dataset = result.getData();*/
        if (IDataUtil.isEmpty(result))
        {
            this.setAjax("ALERT_INFO", "无数据");
            return;
        }else if("0001".equals(result.getData(0).getString("X_RESULTCODE", "")))
        	
        {
        	 boolean isPriv = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(),"NPLimitQueryRs_PRIV");  	            // 设置页面返回数据
             if(isPriv){  
        	// 设置页面返回数据
        	IDataset setdata = new DatasetList(result.getData(0).getString("X_RESULTINFO", ""));
			setInfos(setdata);
            setCondition(inputData);
            setPageCount(setdata.size());
            
             }else{
            	  result.getData(0).put("X_RESULTCODE", "0002");
            	  
             }
        }
        
        this.setAjax(result);
       
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

    public abstract void setPrivateFlag(boolean flag);
    
    

}
