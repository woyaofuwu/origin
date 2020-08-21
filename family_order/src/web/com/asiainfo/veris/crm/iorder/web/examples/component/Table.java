package com.asiainfo.veris.crm.iorder.web.examples.component;


import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;

public abstract class Table extends BizPage{
	
	public abstract void setInfos(IDataset infos);
	
	public void init(IRequestCycle cycle) throws Exception{
		
		IDataset list = new DatasetList();
			
		for(int i = 0; i < 30; i++){
			IData info = new DataMap();
			info.put("TEXT", "选项" + i);
			info.put("TITLE", "title_" + i);
			info.put("VALUE", "value_" + i);
			
			list.add(info);
		}
		
		setInfos(list);
	}
}