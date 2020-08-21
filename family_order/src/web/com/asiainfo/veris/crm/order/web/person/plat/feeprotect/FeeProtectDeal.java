
package com.asiainfo.veris.crm.order.web.person.plat.feeprotect;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FeeProtectDeal extends PersonBasePage
{
    /**
     * 增值业务计费安全开关办理
     * 
     * @param cycle
     * @return 
     * @throws Exception
     */
    public void feeProtectDeal(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IDataset feeProtectDiscntInfos = CSViewCall.call(this, "SS.FeeProtectSVC.getFeeProtectDiscntInfo", userInfo);     
        setFeeProtectDiscntInfos(feeProtectDiscntInfos);
    }
    
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
            
        }                      
        IData param = new DataMap();
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.FeeProtectRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setFeeProtectDiscntInfos(IDataset feeProtectDiscntInfos);
}
