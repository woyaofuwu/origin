package com.asiainfo.veris.crm.order.web.person.zhsyt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ZhsytRcord  extends PersonBasePage  {
	//界面初始化方法
	public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        
    }
	/**
	 * 根据条件查询综合收银台差异记录
	 * @param cycle
	 * @throws Exception
	 * @author zhuweijun
	 */
	public void queryData(IRequestCycle cycle)throws Exception
	{
		IData map=getData();
		Pagination page = this.getPagination("pageNav");
		IDataOutput result = CSViewCall.callPage(this, "SS.ZhsytRecordSVC.queryData", map, page);
		IDataset dataset = result.getData();
		//判断是否存在记录
		if (IDataUtil.isEmpty(result.getData()))
        { 
            setAjax("CODE", "0");
        }
		setZhsytInfos(dataset);
		setCount(result.getDataCount());
        setCond(getData("cond"));
	}
	
	/******************** set ************************/
	 public abstract void setZhsytInfos(IDataset zhsytInfos);
	 public abstract void setCount(long count);
     public abstract void setCond(IData cond);
}
