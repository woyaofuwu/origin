/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.bankpaymentmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-6-24 修改历史 Revision 2014-6-24 上午09:04:06
 */
public abstract class CancelChangeContract extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.BankPaymentManageSVC.loadCancelChangeContractInfo", data);

        this.setCondition(dataset.getData(0));

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
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        if ("1".equals(data.getString("OPER_RADIO_VALUE")))
        {
            this.setAjax(CSViewCall.call(this, "SS.ChangeContractRegSVC.tradeReg", data));
        }
        else if ("0".equals(data.getString("OPER_RADIO_VALUE")))
        {
            this.setAjax(CSViewCall.call(this, "SS.CancelContractRegSVC.tradeReg", data));
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);
}
