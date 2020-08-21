/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.cpe;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * CPE号码解锁
 */
public abstract class CPEAreaUnlock extends PersonBasePage
{

    public void unlockAreaQryInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.CPEActiveSVC.unlockAreaQryInfo", pagedata); 
        this.setAjax(results.getData(0));

    } 
    
    /**
     * 提交
     * */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    { 
        IData data = getData(); 
        IDataset dataset = CSViewCall.call(this, "SS.CPEAreaUnlockRegSVC.tradeReg", data);
        setAjax(dataset);
    } 
}
