package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
public interface IEsopData {
	
	//处理esop信息
	public IDataset actEsopInfo(IData inparam) throws Exception;
	
	//获取esop产品信息
	public void queryOfferInfo() throws Exception;
	
	//获取合同信息
	public void queryContractInfo() throws Exception;
}
