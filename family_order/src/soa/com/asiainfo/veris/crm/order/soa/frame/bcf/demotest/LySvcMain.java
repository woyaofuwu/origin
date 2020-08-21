
package com.asiainfo.veris.crm.order.soa.frame.bcf.demotest;

import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public final class LySvcMain extends BizProcess
{
    public static void main(String[] args) throws Exception
    {
        IData input = new DataMap();
        input.put("STAFF_ID", "SUPERUSR");
        input.put("STAFF_NAME", "SUPERUSR");
        input.put("SERIAL_NUMBER", "15116370001");
        input.put("STAFF_EPARCHY_CODE", Route.getCrmDefaultDb());
        input.put("STAFF_EPARCHY_NAME", Route.getCrmDefaultDb());
        input.put("LOGIN_EPARCHY_CODE", Route.getCrmDefaultDb());
        input.put("LOGIN_EPARCHY_NAME", Route.getCrmDefaultDb());
        input.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
        input.put("DEPART_ID", "00000");
        input.put("DEPART_CODE", "00000");
        input.put("DEPART_NAME", "00000");
        input.put("CITY_CODE", "0000");
        input.put("CITY_NAME", "0000");
        input.put("PROVINCE_CODE", "HNAN");
        input.put("IN_MODE_CODE", "0");
        input.put("REMOTE_ADDR", "0000");
        input.put("SUBSYS_CODE", "saleserv");
        input.put("CANCEL_TAG", "0");

        LySvcMain test = new LySvcMain();
        test.setGroup("groupserv");
        test.setRouteId("0731");
        test.start(input);
    }

    private void breLog(IData input) throws Exception
    {
        for (int i = 0; i < 1; i++)
        {
            LySvc.breLog(input);
        }
    }

    private void chkLog(IData input) throws Exception
    {
        for (int i = 0; i < 1; i++)
        {
            LySvc.chkLog(input);
        }
    }

    private void execCgToCrm(IData input) throws Exception
    {

    }

    private void exptest(IData input) throws Exception
    {
        LySvc.exptest(input);
    }

    private void finish(IData input) throws Exception
    {
        LySvc.finish(input);
    }

    private void fuzzy(IData input) throws Exception
    {
        LySvc.fuzzy(input);
    }

    @Override
    public void run() throws Exception
    {
        IData input = getInput();

        // fuzzy(input);
        finish(input);
    }

    public void sendTest(IData input) throws Exception
    {
        LySvc.sendpf(input);
    }
}
