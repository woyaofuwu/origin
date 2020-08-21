package com.asiainfo.veris.crm.order.soa.frame.bof.callPf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public interface ICallPfDeal {

	public IDataset dealPfData(IData input)throws Exception;
}
