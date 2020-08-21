/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.bankpaymentmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by xiaobin
 */
public abstract class CancelSignBank extends PersonBasePage
{
	public abstract void setInfo(IData idata);

	public abstract void setCondition(IData idata);

	public abstract void setBanks(IDataset bankList);
	
	public abstract void setHoldList(IDataset holdList);//充值阀值

	public abstract void setAmountList(IDataset amountList);//充值额度
	
    public void loadInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	
    	IDataset datas = CSViewCall.call(this, "SS.CancelSignBankSVC.loadInfos", data);
    	
    	setBanks(datas);
    	
    }
    

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
//        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CancelSignBankRegSVC.tradeReg", data);

        this.setAjax(dataset);
    }

    public abstract void setInfos(IDataset infos);

}
