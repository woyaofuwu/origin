
package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TestGrpMemberOpenTrans extends BizProcess
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

        // V网
        /*
         * inparam.put("SERIAL_NUMBER", "13807502777"); inparam.put("USER_ID_A", "1111080428470631");
         * inparam.put("PRODUCT_ID", "8000"); StringBuilder sb2 = new StringBuilder(100);
         * sb2.append("800001,80000102,1286"); inparam.put("DISCNT_CODE", sb2.toString());
         * inparam.put("SHORT_CODE","6777");
         */

        // 彩铃
        /*
         * inparam.put("SERIAL_NUMBER", "13807502777"); inparam.put("USER_ID_A", "1111080428470723");
         * inparam.put("PRODUCT_ID", "6200");
         */
        // 集团产品受理时的用户地州为交易地州

        // ADC利客发产品产品办理 9130
        /*
         * inparam.put("SERIAL_NUMBER", "13807502777"); inparam.put("USER_ID_A", "1114062722684942");
         * inparam.put("PRODUCT_ID", "9130"); StringBuilder sb2 = new StringBuilder(100);
         * sb2.append("913001,91300101,91300103"); inparam.put("SERVICE_CODE", sb2.toString());
         */

        // 10005742
        /*
         * inparam.put("SERIAL_NUMBER", "15719806231"); inparam.put("USER_ID_A", "1112102324687835");
         * inparam.put("PRODUCT_ID", "10005742"); StringBuilder sb2 = new StringBuilder(100);
         * sb2.append("574201,57420102,3040"); inparam.put("DISCNT_CODE", sb2.toString()); IData productParmas=new
         * DataMap(); productParmas.put("stu_name1","的1"); productParmas.put("stu_name2","的2");
         * productParmas.put("stu_name3","的3"); productParmas.put("stu_phone1","13007314567");
         * productParmas.put("stu_phone2","13007314568"); productParmas.put("stu_phone3","13007314569"); IData
         * productParmas_result=new DataMap(); productParmas_result.put("10005742", productParmas);
         * inparam.put("PRODUCT_PARAM", productParmas_result);
         */

        /*
         * StringBuilder sb3 = new StringBuilder(100); sb3.append("913001,91300101,91300103");
         * inparam.put("SERVICE_CODE", sb3.toString());
         */

        // 产品参数

        // 10009150
        inparam.put("BUSI_SIGN", "BIP4B248_T2101708_1_0");

        inparam.put("MOB_NUM", "13627540477");
        inparam.put("OPR_CODE", "01");
        inparam.put("POINT_CODE", "4303||4302");
        inparam.put("stu_name1", "张三");
        inparam.put("stu_name2", "李四");
        inparam.put("servicephone", "18907651234");
        inparam.put("SERV_CODE", "10657018000027");
        inparam.put("BIZ_CODE", "AHI3911602");

        inparam.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");

        TestGrpMemberOpenTrans testGrpUseOpen = new TestGrpMemberOpenTrans();
        testGrpUseOpen.setRouteId("0898");
        testGrpUseOpen.setGroup("saleserv");
        testGrpUseOpen.start(inparam);

    }

    @Override
    public void run() throws Exception
    {
        // 校讯通10005742
        // IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/saleserv/service",
        // "SS.CreateAdcGroupMemberSVC.crtOrder", getInput(), false);

        // 10009150
        IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/saleserv/service", "SS.TcsGrpIntfSVC.dealAdcMasMemBiz", getInput(), false);
    }
}
