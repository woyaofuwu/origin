package com.asiainfo.veris.crm.order.web.person.cttTerminalSale;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryTerminalInfo extends PersonBasePage{

	public void init(IRequestCycle cycle) throws Exception
    {
		
    }
	
	public void qryTerminalInfo(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        IDataOutput result = CSViewCall.callPage(this, "PB.MaterialChooseSvc.queryMaterialListByCond", param, this.getPagination("pagin"));
        
        IDataset dataSet=result.getData();
        if(IDataUtil.isNotEmpty(dataSet)){
        	this.setPaginCount(dataSet.size());
        }else{
        	this.setPaginCount(0);
        }
        
        this.setAjax(result.getData());
        this.setInfos(result.getData());
    }
	
	public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long count);
}
