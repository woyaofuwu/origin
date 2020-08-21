
package com.asiainfo.veris.crm.order.web.person.einvoicehistory;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class EInvoiceHistory extends PersonBasePage
{


    /**
     * 查询历史电子发票
     */
	  public void queryEInvoiceTrade(IRequestCycle cycle) throws Exception
      {
        IData data = getData();
        IDataset reprintTrades = CSViewCall.call(this, "SS.EInvoiceHistorySVC.queryEInvoiceTrade", data);
        setInfos(reprintTrades);
        setCond(data);
        setAjax(reprintTrades);
      }
    
    /**
     * 发票冲红
     * @param cond
     */
    public void toCreditNote (IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset reprintTrades = CSViewCall.call(this, "SS.EInvoiceHistorySVC.modifyEInvoiceTrade", data);
        
    	
    }

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
