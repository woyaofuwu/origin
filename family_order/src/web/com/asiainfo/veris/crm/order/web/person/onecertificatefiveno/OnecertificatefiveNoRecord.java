package com.asiainfo.veris.crm.order.web.person.onecertificatefiveno;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OnecertificatefiveNoRecord extends PersonBasePage {
	//界面初始化方法
	public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        
    }
	/**
	 * 根据条件查询一证五号对账差异记录
	 * @param cycle
	 * @throws Exception
	 * @author huangzl3
	 */
	public void queryData(IRequestCycle cycle)throws Exception
	{
		IData map=getData();
		Pagination page = this.getPagination("pageNav");
		IDataOutput result = CSViewCall.callPage(this, "SS.OnecertificatefiveNoRecordSVC.queryData", map, page);
		IDataset dataset = result.getData();
		//判断是否存在记录
		if (IDataUtil.isEmpty(result.getData()))
        { 
            setAjax("CODE", "0");
        }
		setInfos(dataset);
		setCount(result.getDataCount());
        //setCond(getData("cond"));
	}
	
	public void dealOnecertificatefiveNoInfo(IRequestCycle cycle) throws Exception{
		IData idata = getData();
		CSViewCall.call(this, "SS.OnecertificatefiveNoRecordSVC.dealOnecertificatefiveNoInfo", idata);	
	}
	
	
	/******************** set ************************/
	 public abstract void setInfos(IDataset infos);
	 public abstract void setCount(long count);
     //public abstract void setCond(IData cond);
}
