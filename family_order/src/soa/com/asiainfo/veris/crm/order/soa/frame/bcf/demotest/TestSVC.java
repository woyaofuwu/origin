
package com.asiainfo.veris.crm.order.soa.frame.bcf.demotest;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TestSVC extends BizProcess
{

    public static void main(String[] args) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put(BizConstants.STAFF_ID, "SUPERUSR");
        inparam.put(BizConstants.STAFF_NAME, "SUPERUSR");
        inparam.put(BizConstants.SERIAL_NUMBER, "12553317377");
        inparam.put(BizConstants.STAFF_EPARCHY_CODE, "0022");
        inparam.put(BizConstants.STAFF_EPARCHY_NAME, "0022");
        inparam.put(BizConstants.LOGIN_EPARCHY_CODE, "0022");
        inparam.put(BizConstants.LOGIN_EPARCHY_NAME, "0022");
        inparam.put(BizConstants.ROUTE_EPARCHY_CODE, "0022");
        inparam.put(BizConstants.DEPART_ID, "36601");
        inparam.put(BizConstants.DEPART_CODE, "HNSJ0000");
        inparam.put(BizConstants.DEPART_NAME, "移动省公司");
        inparam.put(BizConstants.CITY_CODE, "HNSJ");
        inparam.put(BizConstants.CITY_NAME, "省局");
        inparam.put(BizConstants.PROVINCE_CODE, "0022");
        inparam.put(BizConstants.IN_MODE_CODE, "0");
        inparam.put(BizConstants.REMOTE_ADDR, "192.168.97.146");
        inparam.put(BizConstants.SUBSYS_CODE, "groupserv");

        inparam.put("ORDER_ID", "1115012800166495");
        inparam.put("ACCEPT_MONTH", "1"); 
        inparam.put("CANCEL_TAG", "0");

        // 集团产品受理时的用户地州为交易地州
        inparam.put(Route.USER_EPARCHY_CODE, "0022");

        TestSVC testsvc = new TestSVC();
        testsvc.setRouteId("0022");
        testsvc.setGroup("saleserv");
        testsvc.start(inparam);
    }

    @Override
    public void run() throws Exception
    {
        IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/service", "CS.TradePfSVC.sendPf", getInput(), false);
    }
}
