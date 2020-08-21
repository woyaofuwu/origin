
package com.asiainfo.veris.crm.order.web.person.changetdcard;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.person.simcardmgr.ChangeCard;

public abstract class ChangeTDCard extends ChangeCard
{
    protected static Logger log = Logger.getLogger(ChangeTDCard.class);

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    @Override
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        data.put("ORDER_TYPE_CODE", "3821");
        data.put("TRADE_TYPE_CODE", "3821");
        IDataset dataset = CSViewCall.call(this, "SS.ChangeCardSVC.tradeReg", data);
        setAjax(dataset);
    }
}
