
package com.asiainfo.veris.crm.order.soa.group.demo.bof;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TranTest extends BizProcess
{

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        IData input = new DataMap();
        input.put("BUSI_SIGN", "BIP4B248_T4101025"); // BIP4B248_T4101025
        input.put("MOB_NUM", "13976280069"); //
        input.put("SERV_CODE", "106581236");
        input.put("BIZ_CODE", "1065812364");
        input.put("ECID", "8989859416");
        input.put("OPR_CODE", "01");

        //
        input.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");
        input.put(BizConstants.TRADE_DEPART_ID, "36601");
        input.put(BizConstants.DEPART_CODE, "00000");
        input.put(BizConstants.TRADE_STAFF_ID, "SUPERUSR");
        input.put(BizConstants.STAFF_NAME, "SUPERUSR");
        input.put(BizConstants.DEPART_ID, "00000");
        input.put(BizConstants.STAFF_ID, "SUPERUSR");
        input.put(BizConstants.STAFF_NAME, "SUPERUSR");
        input.put(BizConstants.STAFF_EPARCHY_CODE, "0898");
        input.put(BizConstants.STAFF_EPARCHY_NAME, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_CODE, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_NAME, "0898");
        input.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");
        input.put(BizConstants.CITY_CODE, "0898");

        TranTest test = new TranTest();
        test.setGroup("groupserv");
        test.setRouteId("0898");
        test.start(input);
    }

    @Override
    public void run() throws Exception
    {

        CSAppCall.call("http://127.0.0.1:8080/service", "SS.TcsGrpIntfSVC.dealAdcMasMemBiz", getInput(), false);

    }

}
