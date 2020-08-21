package com.asiainfo.veris.crm.order.soa.group.receipt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReceiptinfoSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    
    public IDataset qryreceiptinfo(IData map)throws Exception
    {
    	String trade_id = map.getString("TRADE_ID");
    	return Qryreceiptinfo.qryreceiptinfo(trade_id);
    }

}
