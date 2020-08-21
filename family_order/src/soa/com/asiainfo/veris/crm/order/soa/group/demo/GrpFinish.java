
package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GrpFinish extends BizProcess
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        IData input = new DataMap();
        input.put("TRADE_ID", "1194032039985903");
        input.put("ACCEPT_MONTH", "3");
        input.put("CANCEL_TAG", "0");
        input.put(Route.ROUTE_EPARCHY_CODE, "0898");
        input.put(Route.USER_EPARCHY_CODE, "0898");
        GrpFinish test = new GrpFinish();
        test.setRouteId("cg");
        test.setGroup("groupserv_hain");
        test.start(input);
    }

    @Override
    public void run() throws Exception
    {
        CSAppCall.call("http://127.0.0.1:8080/groupserv_hain/service", "CS.TradeFinishSVC.finish", getInput(), false);

    }
}
