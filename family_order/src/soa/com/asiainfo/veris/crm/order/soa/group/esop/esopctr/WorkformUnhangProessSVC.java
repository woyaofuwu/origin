package com.asiainfo.veris.crm.order.soa.group.esop.esopctr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class WorkformUnhangProessSVC extends GroupOrderService 
{
private static final long serialVersionUID = -3923226370322319051L;
	

	
	
	public IDataset queryInfo(IData inparam) throws Exception
	{
		IDataset resultInfos = new DatasetList();

		IData temp = new DataMap();
		temp.put("RELE_VALUE", "1");
		temp.put("RELE_CODE", "HANGCITY_NUM");

		resultInfos.add(temp);
		
		temp = new DataMap();
		temp.put("RELE_VALUE", "2");
		temp.put("RELE_CODE", "HANGCITY_NUM");
		resultInfos.add(temp);
		return resultInfos;
	}
	
	
}
