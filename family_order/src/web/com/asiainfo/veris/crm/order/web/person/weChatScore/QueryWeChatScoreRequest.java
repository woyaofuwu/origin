package com.asiainfo.veris.crm.order.web.person.weChatScore;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryWeChatScoreRequest extends PersonBasePage{

	public void init(IRequestCycle cycle) throws Exception
    {
		
    }
	
	public void qryWechatScoreRequest(IRequestCycle cycle) throws Exception
    {
		IData param = this.getData("cond", true);
		param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		IDataOutput result=CSViewCall.callPage(this, "SS.WeChatScoreSVC.queryWeChatScoreRequest", param, this.getPagination("page"));
		
        IDataset dataSet=result.getData();
        if(IDataUtil.isNotEmpty(dataSet)){
        	this.setPaginCount(result.getDataCount());
        	this.setInfos(result.getData()); 
        	this.setAjax(result.getData());
        }else{
        	this.setPaginCount(0);
        	this.setInfos(new DatasetList()); 
        	this.setAjax(new DatasetList());
        }
		
        
       
        
//        this.setPaginCount(result.getDataCount());
    }
	
	
	public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long count);
}
