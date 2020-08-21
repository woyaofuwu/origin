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
 * @CREATED by gongp@2014-5-27 修改历史 Revision 2014-5-27 下午03:46:13
 */
public abstract class SignContract extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

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
        IDataset dataset = CSViewCall.call(this, "SS.SignContractRegSVC.tradeReg", data);

        this.setAjax(dataset);
    }

    public abstract void setInfos(IDataset infos);

}
