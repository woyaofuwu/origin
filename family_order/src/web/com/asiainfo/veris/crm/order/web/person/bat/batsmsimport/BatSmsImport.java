package com.asiainfo.veris.crm.order.web.person.bat.batsmsimport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tapestry.event.PageEvent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatSmsImport extends PersonBasePage{

	@Override
	public void pageBeginRender(PageEvent event) {
		// TODO Auto-generated method stub
		super.pageBeginRender(event);
		
		init();
	}

	public void init() 
	{
		IData inparam = new DataMap();
		
		IDataset out;
		try {
			out = CSViewCall.call(this, "SS.BatSmsImport.GetWellNum", inparam );
			IData cond = new DataMap();
			cond.put("group_num", out);
			setCond(cond);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract void setCond(IData cond);
	
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setListCount(long listCount);
	
}
