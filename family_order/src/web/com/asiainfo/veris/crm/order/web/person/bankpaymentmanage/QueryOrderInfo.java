/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.bankpaymentmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by xiaobin
 */
public abstract class QueryOrderInfo extends PersonBasePage
{
	public abstract void setInfo(IData idata);

	public abstract void setCondition(IData idata);

	public abstract void setOrderResults(IDataset orderResults);
	
    public abstract void setPaginCount(long count);

	
    public void queryResultsInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
//    	IDataset orderDataSet = CSViewCall.call(this, "SS.QueryOrderInfoSVC.queryResultsInfo", data);
    	IDataOutput result = CSViewCall.callPage(this, "SS.QueryOrderInfoSVC.queryResultsInfo", data, this.getPagination("navt"));
    	
    	setPaginCount(result.getDataCount());
    	setOrderResults(result.getData());
    	setAjax(result.getData());
    	setCondition(data);
    }


}
