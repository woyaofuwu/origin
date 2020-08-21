/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.ncardsoneacct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 下午08:00:05
 */
public abstract class NcardsOneAcctSale extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        userInfo.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this,"0", "S", userInfo.getString("USER_STATE_CODESET", "")));
        CSViewCall.call(this, "SS.NcardsOneAcctSVC.loadSaleChildInfo", userInfo);
        this.setCustInfoView(custInfo);
        this.setUserInfoView(userInfo);

    }

    public void loadSecondNumInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IDataset results = CSViewCall.call(this, "SS.NcardsOneAcctSVC.getSecondNumUcaInfo", data);
        IData userInfo = results.getData(0).getData("USER_INFO");
        userInfo.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this,"0", "S",userInfo.getString("USER_STATE_CODESET", "")));
        this.setCustInfoView2(results.getData(0).getData("CUST_INFO"));
        this.setUserInfoView2(userInfo);
        this.setAjax(results.getData(0).getData("WARN_INFO"));
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

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

        this.setAjax(CSViewCall.call(this, "SS.NcardsOneAcctSaleRegSVC.tradeReg", data));
    }

    public abstract void setCustInfoView2(IData cond);

    public abstract void setUserInfoView2(IData cond);

}
