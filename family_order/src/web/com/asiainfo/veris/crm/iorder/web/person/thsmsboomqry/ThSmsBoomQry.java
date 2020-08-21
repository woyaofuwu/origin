package com.asiainfo.veris.crm.iorder.web.person.thsmsboomqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThSmsBoomQry extends PersonBasePage
{
    public abstract void setCondition(IData condition);

    public abstract void setSmsBoomTradeList(IDataset unpaidTrade);
    
	public void querySmsTrade(IRequestCycle cycle)throws Exception
	{
		IData data = this.getData();
		IData condition = new DataMap();
		condition.put("ACCESS_NO", data.getString("cond_ACCESS_NO"));
		condition.put("SERIAL_NO", data.getString("cond_SERIAL_NO"));
		if("".equals(data.getString("cond_SERIAL_NO",""))){
			condition.put("SERIAL_NUMBER", data.getString("cond_ACCESS_NO"));
		}else{
			condition.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NO"));
		}
        IDataset smsBoomTradeInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThSmsBoomInfoNew", condition);       
        this.setSmsBoomTradeList(smsBoomTradeInfo);
	}
}
