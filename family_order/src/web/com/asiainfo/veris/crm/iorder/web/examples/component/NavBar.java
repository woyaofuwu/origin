package com.asiainfo.veris.crm.iorder.web.examples.component;


import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;


public abstract class NavBar extends BizPage {

	public abstract void setInfos(IDataset infos);
	public abstract void setInfoCount(long infoCount);
	
	
	private static long total = 100;
	
	public void queryList(IRequestCycle cycle) throws Exception{
		
		Pagination pagin = getPagination("navbar1");
		
		int page = pagin.getCurrent();
		int pageSize = pagin.getPageSize();
		
		IDataset list = new DatasetList();
		for(int i = 0; i < pageSize; i++){
			IData info = new DataMap();
			info.put("PAGE_INFO", page + "|" + i);
			list.add(info);
		}
		
		setInfos(list);
		setInfoCount(total);
	}
}
