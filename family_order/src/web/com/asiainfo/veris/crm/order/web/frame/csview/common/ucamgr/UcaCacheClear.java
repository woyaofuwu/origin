
package com.asiainfo.veris.crm.order.web.frame.csview.common.ucamgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class UcaCacheClear extends CSBasePage
{

    /**
     * ajax传参方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void ajaxSetPospecNumber(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 根据SERIAL_NUMBER获取各缓存号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void clear(IRequestCycle cycle) throws Exception
    {
        String serialNumber = getData().getString("SERIAL_NUMBER");
        String clearType = getData().getString("CLEAR_TYPE");

        IData param = new DataMap();

        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CLEAR_TYPE", clearType);

        IDataset dataset = CSViewCall.call(this, "CS.UcaCookieClearSVC.clear", param);

        setInfos(dataset);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData Info);

    public abstract void setInfos(IDataset infos);

    public abstract void setResultCode(String resultCode);

    public abstract void setResultInfos(String resultInfos);

    public abstract void setRowIndex(int rowIndex);

}
