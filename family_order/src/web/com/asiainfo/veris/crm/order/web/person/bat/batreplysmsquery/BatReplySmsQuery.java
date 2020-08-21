package com.asiainfo.veris.crm.order.web.person.bat.batreplysmsquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatReplySmsQuery extends PersonBasePage{
	 public abstract IDataset getInfos();
	 
	
    public void batreplysmsquery(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.BatReplySmsQuerySVC.batReplySmsQuery", inparam, getPagination("navt"));
        IDataset results = dataCount.getData();
        setInfos(results);
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
