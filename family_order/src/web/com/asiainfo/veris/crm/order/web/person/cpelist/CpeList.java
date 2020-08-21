package com.asiainfo.veris.crm.order.web.person.cpelist;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CpeList extends PersonBasePage{
	
	private static Logger logger = Logger.getLogger(CpeList.class);
	
    public void queryImportData(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        String alertInfo = "";
        IDataOutput infos = CSViewCall.callPage(this, "SS.CpeListSVC.queryImportData", data, getPagination("pagin"));

        if (IDataUtil.isEmpty(infos.getData()))
        {
            alertInfo = "查询无数据!";
        }

        setInfos(infos.getData());
        setCount(infos.getDataCount());

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }
    
	 public abstract void setCond(IData cond);
	 public abstract void setInfos(IDataset dataset);
	 public abstract void setCount(long count);
}
