
package com.asiainfo.veris.crm.order.web.group.creategroupacct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpEInvoiceHistory extends GroupBasePage
{
	 public void queryByGroupId(IRequestCycle cycle) throws Exception
	    {
	        IData data = getData("cond", true);
	        IData params=new DataMap();
	        params.put("GROUP_ID", data.getString("GROUP_ID"));
	        params.put("START_DATE", data.getString("START_DATE"));
	        params.put("END_DATE", data.getString("END_DATE"));
	        params.put("PRINT_FLAG", data.getString("PRINT_FLAG"));
	        IDataset reprintTrades=CSViewCall.call(this, "CS.GrpEinvoiceHismanagerSVC.queryByGroupId",params);
	        setInfos(reprintTrades);
	        setAjax(reprintTrades);
	    }

    /**
     * 发票冲红
     * @param cond
     */
    public void toCreditNote (IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
         CSViewCall.call(this, "CS.GrpEinvoiceHismanagerSVC.modifyEInvoiceTrade", data);
        
    	
    }

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
