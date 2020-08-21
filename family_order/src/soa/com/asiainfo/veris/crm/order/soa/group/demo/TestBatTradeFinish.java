
package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TestBatTradeFinish extends BizProcess
{

    public static void main(String[] args) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put(BizConstants.STAFF_ID, "SUPERUSR");
        inparam.put(BizConstants.STAFF_NAME, "SUPERUSR");
        inparam.put(BizConstants.STAFF_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.STAFF_EPARCHY_NAME, "0898");
        inparam.put(BizConstants.LOGIN_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.LOGIN_EPARCHY_NAME, "0898");
        inparam.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.DEPART_ID, "36601");
        inparam.put(BizConstants.DEPART_CODE, "HNSJ0000");
        inparam.put(BizConstants.DEPART_NAME, "移动省公司");
        inparam.put(BizConstants.CITY_CODE, "HNSJ");
        inparam.put(BizConstants.CITY_NAME, "省局");
        inparam.put(BizConstants.PROVINCE_CODE, "xxxx");
        inparam.put(BizConstants.IN_MODE_CODE, "v");
        inparam.put(BizConstants.REMOTE_ADDR, "127.0.0.1");
        inparam.put(BizConstants.SUBSYS_CODE, "groupserv");

        inparam.put("BATCH_TASK_ID", "1117040783815401");
        // inparam.put("SERIAL_NUMBER", "13627567414");

        // 集团产品受理时的用户地州为交易地州
        inparam.put(Route.USER_EPARCHY_CODE, "0898");
        TestBatTradeFinish testGrpUseOpen = new TestBatTradeFinish();
        testGrpUseOpen.setRouteId("0898");
        testGrpUseOpen.setGroup("saleserv");
        testGrpUseOpen.start(inparam);

    }

    @Override
    public void run() throws Exception
    {
        CSAppCall.call("http://localhost:8080/order/service", "SS.BatTradeFinishSVC.finish", getInput(), false);

    }
}
