package com.asiainfo.veris.crm.order.web.person.cpe;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * REQ201601120007 CPE无线宽带查询用户使用业务所在小区界面
 * chenxy3 2016-01-15
 * */
public abstract class CPEDataAmountQry extends PersonBasePage
{

    public void cpeDataInfoQry(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.CPEActiveSVC.dataAmountQry", pagedata); 
        IData rtnData=new DataMap();
        if(results!=null && results.size()>0){
        	rtnData=results.getData(0);
        	String code=rtnData.getString("CODE","");
        	if("2".equals(code)){
        		this.setAjax(rtnData);
        	}else{
        		this.setInfos(results);
        	}
        }         
    } 
    public abstract void setCond(IData cond);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
