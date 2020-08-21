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
public abstract class CreateSignBank extends PersonBasePage
{
	public abstract void setInfo(IData idata);

	public abstract void setCondition(IData idata);

	public abstract void setBanks(IDataset bankList);
	
	public abstract void setHoldList(IDataset holdList);//充值阀值

	public abstract void setAmountList(IDataset amountList);//充值额度
	
    public void loadInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	
    	IData datas = CSViewCall.callone(this, "SS.CreateSignBankSVC.loadInfos", data);
    	
    	IDataset signBanks = datas.getDataset("signBanks");
    	IDataset holdList = datas.getDataset("holdList");
    	IDataset amountList = datas.getDataset("amountList");
    	
    	setBanks(signBanks);
    	setHoldList(holdList);
    	setAmountList(amountList);
    	
    }
    
    public void checkSignBank(IRequestCycle cycle) throws Exception{
    	
    	IData inparam = getData();
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreateSignBankSVC.checkSignBank", inparam);
    	
    	setAjax(dataset);
    }
    
    

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //ystem.out.println("submit data============="+data);
        
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = CSViewCall.call(this, "SS.CreateSignBankRegSVC.tradeReg", data);

        this.setAjax(dataset);
    }

    public abstract void setInfos(IDataset infos);

}
