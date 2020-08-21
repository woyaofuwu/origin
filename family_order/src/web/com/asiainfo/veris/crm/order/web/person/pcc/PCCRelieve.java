/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.pcc;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 限速解速界面
 */
public abstract class PCCRelieve extends PersonBasePage
{

    public void unlockAreaQryInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.PCCRelieveSVC.unlockAreaQryInfo", pagedata); 
        this.setAjax(results.getData(0));

    } 
    
    /**
     * 提交
     * */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    { 
        IData data = getData(); 
        IDataset dataset = CSViewCall.call(this, "SS.PCCRelieveRegSVC.tradeReg", data);
        setAjax(dataset);
    } 
}
