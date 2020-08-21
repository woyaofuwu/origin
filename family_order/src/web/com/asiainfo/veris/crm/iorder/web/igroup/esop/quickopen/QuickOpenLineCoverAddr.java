package com.asiainfo.veris.crm.iorder.web.igroup.esop.quickopen;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;


public abstract class QuickOpenLineCoverAddr extends CSBasePage
{

	 public abstract void setInfos(IDataset infos);
	 public abstract void setInfo(IData info);
	 public abstract void setCondition(IData condition);
	 public abstract void setCount(long count);
	 
	    /**
	     * 查询快速覆盖开通地址
	     * @param cycle
	     * @throws Exception
	     */
	    public void queryLineCoverAddr(IRequestCycle cycle) throws Exception
	    {
	        IData data = new DataMap();
	        IData pagedata= getData("cond",true);
	        data.put("STANDARD_ADDR", pagedata.getString("STANDARD_ADDR"));
	        data.put("COVERTAG", pagedata.getString("COVERTAG"));
	        IDataOutput output = CSViewCall.callPage(this, "SS.LineAddrQrySVC.queryLineCoverAddr", data, this.getPagination("queryNav"));
	        setCount(output.getDataCount());
	        setInfos(output.getData());
	    }
}
