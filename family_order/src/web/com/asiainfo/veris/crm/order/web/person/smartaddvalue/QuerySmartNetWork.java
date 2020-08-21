package com.asiainfo.veris.crm.order.web.person.smartaddvalue;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QuerySmartNetWork extends PersonBasePage{ 
    /**
     * 查询提交
     * 
     * @param cycle
     * @throws Exception
     */
	public abstract void setInfos(IDataset infos);
	/**
	 *   
	 * @param cycle
	 * @throws Exception
	 */
	public void querySmartNetwork(IRequestCycle cycle) throws Exception
	{
 
		IData data = getData("cond", true);
		//获取用户输入数据前缀cond
		String serailNumber = data.getString("SERIAL_NUMBER", "");
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", serailNumber);        
        IDataset dataset = CSViewCall.call(this, "SS.QuerySmartNetworkRegSVC.querySmartNetwork", input);     
        setInfos(dataset);
	}
	 public void onInitQuery(IRequestCycle cycle) throws Exception
	    {
	         
	    }
	    //public abstract void setAuditInfos(IDataset dataset);
	    
	    
}