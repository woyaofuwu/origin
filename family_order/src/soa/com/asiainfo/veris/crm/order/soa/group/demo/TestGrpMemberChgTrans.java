
package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TestGrpMemberChgTrans extends BizProcess
{
    public static void main(String[] args) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put(BizConstants.TRADE_STAFF_ID, "SUPERUSR");
        inparam.put(BizConstants.STAFF_NAME, "SUPERUSR");
        // inparam.put(BizConstants.SERIAL_NUMBER, "12553317377");
        inparam.put(BizConstants.STAFF_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.STAFF_EPARCHY_NAME, "0898");
        // inparam.put(BizConstants.LOGIN_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.TRADE_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.TRADE_DEPART_ID, "36601");
        inparam.put(BizConstants.DEPART_CODE, "HNSJ0000");
        inparam.put(BizConstants.DEPART_NAME, "移动省公司");
        inparam.put(BizConstants.TRADE_CITY_CODE, "HNSJ");
        inparam.put(BizConstants.CITY_NAME, "省局");
        inparam.put(BizConstants.PROVINCE_CODE, "xxxx");
        inparam.put(BizConstants.IN_MODE_CODE, "2");
        inparam.put(BizConstants.REMOTE_ADDR, "192.168.97.146");
        inparam.put(BizConstants.SUBSYS_CODE, "groupserv");

        // v网
        /*
         * inparam.put("SERIAL_NUMBER", "15719806231"); inparam.put("SERIAL_NUMBER_A", "V0SJ003989");
         * inparam.put("PRODUCT_ID", "8000"); inparam.put("MODIFY_TAG", "2"); inparam.put("DISCNT_CODE_B","1285");
         * inparam.put("DISCNT_CODE_A","1286"); inparam.put("EFFECT_TIME_TAG","1");
         */

        inparam.put("SERIAL_NUMBER", "15719806231");
        inparam.put("SERIAL_NUMBER_A", "V0SJ003989");
        inparam.put("MODIFY_TAG", "1");

        // ADC利客发产品产品办理 9130
        /*
         * inparam.put("SERIAL_NUMBER", "13807502777"); inparam.put("USER_ID_A", "1114062722684942");
         * inparam.put("PRODUCT_ID", "9130"); StringBuilder sb2 = new StringBuilder(100);
         * sb2.append("913001,91300101,91300103"); inparam.put("SERVICE_CODE", sb2.toString());
         */

        // 集团产品受理时的用户地州为交易地州

        TestGrpMemberChgTrans testGrpUseOpen = new TestGrpMemberChgTrans();
        testGrpUseOpen.setRouteId("0898");
        testGrpUseOpen.setGroup("saleserv");
        testGrpUseOpen.start(inparam);

    }

    @Override
    public void run() throws Exception
    {
        // IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/saleserv/service",
        // "CS.ChangeMemElementSvc.changeMemElement", getInput(), false);

        // callAcct
        // IDataset idataset = CSAppCall.call("http://10.200.130.83:10000/service", "AM_CRM_QryPayItemCode", getInput(),
        // false);

        IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/saleserv/service", "SS.TcsGrpIntfSVC.processGrpMemVpmn", getInput(), false);

    }
}
