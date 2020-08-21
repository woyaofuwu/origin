
package com.asiainfo.veris.crm.order.web.person.getgiftadvancepay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GetGiftAdvancePay extends PersonBasePage
{
    /**
     * 查询服务号码后校验业务受理前限制
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkAcceptTrade(IRequestCycle cycle) throws Exception
    {
        IData input = getData();
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
        IDataset result = CSViewCall.call(this, "SS.GetGiftAdvancePaySVC.checkAcceptTrade", input);
        setAjax(result.getData(0));
    }

    /**
     * 登记台账
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData input = getData();
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
        IDataset result = CSViewCall.call(this, "SS.GetGiftAdvancePayRegSVC.tradeReg", input);
        setAjax(result);
    }

}
