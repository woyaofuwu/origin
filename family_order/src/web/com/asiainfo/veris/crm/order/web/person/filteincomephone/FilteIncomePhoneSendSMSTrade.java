/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.filteincomephone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-4-25 修改历史 Revision 2014-4-25 上午09:33:18
 */
public abstract class FilteIncomePhoneSendSMSTrade extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));

        IDataset results = CSViewCall.call(this, "SS.FilteIncomePhoneSVC.getFilteIncomePhoneSendSMSInfo", userInfo);

        this.setCommInfo(results.getData(0));

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.FilteIncomePhoneSendSMSTradeRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData info);// 

    public abstract void setInfos(IDataset infos);// 

}
