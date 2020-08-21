package com.asiainfo.veris.crm.order.web.person.np.npcheck;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpCheck extends PersonBasePage{
	public void queryImportData(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        String alertInfo = "";
        IDataOutput infos = CSViewCall.callPage(this, "SS.NpCheckSVC.queryImportData", data, getPagination("pagin"));

        if (IDataUtil.isEmpty(infos.getData()))
        {
            alertInfo = "查询无数据!";
        }

        setInfos(infos.getData());
        setCount(infos.getDataCount());

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }
       
    public void submitNpCheck(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        IDataset temp = new DatasetList(inputData.getString("CHECK_DATAS"));
        int tempSize = temp.size();
        IData inparam = new DataMap();
        IDataset result =  new DatasetList();
        for (int i = 0; i < tempSize; i++)
        {
        	inparam.put("SERIAL_NUMBER", temp.getData(i).getString("SERIAL_NUMBER"));
        	result=CSViewCall.call(this, "SS.NpCheckSVC.modifyState", inparam);
        	inparam.clear();
        }
        setAjax(result);
        
    }
    
    public void importBatData(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        CSViewCall.call(this, "SS.NpCheckSVC.importBatData", pageData);
    }
    
	 public abstract void setCond(IData cond);
	 public abstract void setInfos(IDataset dataset);
	 public abstract void setCount(long count);
}
