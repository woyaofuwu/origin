package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TestDealAdcMasMemBiz extends BizProcess
{

    public static void main(String[] args)
    {
        IData inparam = new DataMap();

        inparam.put(BizConstants.STAFF_ID, "IBOSS000");
        inparam.put(BizConstants.STAFF_NAME, "IBOSS000");
        inparam.put(BizConstants.STAFF_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.STAFF_EPARCHY_NAME, "0898");
        inparam.put(BizConstants.LOGIN_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.LOGIN_EPARCHY_NAME, "0898");
        inparam.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");
        inparam.put(BizConstants.DEPART_ID, "12345");
        inparam.put(BizConstants.DEPART_CODE, "HNSJ0000");
        inparam.put(BizConstants.DEPART_NAME, "移动省公司");
        inparam.put(BizConstants.CITY_CODE, "HNSJ");
        inparam.put(BizConstants.CITY_NAME, "省局");
        inparam.put(BizConstants.PROVINCE_CODE, "xxxx");
        inparam.put(BizConstants.IN_MODE_CODE, "PXXT");
        inparam.put(BizConstants.REMOTE_ADDR, "127.0.0.1");
        inparam.put(BizConstants.SUBSYS_CODE, "order");

        inparam.put("POINT_CODE", "4329");
        inparam.put("ROUTE_EPARCHY_CODE", "0898");
        inparam.put("USER_ID_A", "1114091024128652");
        inparam.put("DEALTIME", "2017-04-19 14:42:07");
        inparam.put("OPR_CODE", "01");
        inparam.put("DIRECTION", "PXXT");
        inparam.put("stu_name1", "曹操1");
        inparam.put("OPR_SEQ", "1117041906836220");
        inparam.put("BUSI_SIGN", "BIPXXT05_TX100005_1_0");
        inparam.put("BIZ_SERV_CODE", "10657018000397");
        inparam.put("ECID", "8981100130");
        inparam.put("MOB_NUM", "15203000373");
        inparam.put("FEE_MOB_NUM", "15203000373");
        inparam.put("EFFT_T", "20150731100125");
        
        // 集团产品受理时的用户地州为交易地州
        inparam.put(Route.USER_EPARCHY_CODE, "0898");
        TestDealAdcMasMemBiz testTcsGrpIntf = new TestDealAdcMasMemBiz();
        testTcsGrpIntf.setRouteId("0898");
        testTcsGrpIntf.setGroup("saleserv");
        testTcsGrpIntf.start(inparam);

    }

    @Override
    public void run() throws Exception
    {
        CSAppCall.call("http://localhost:8080/order/service", "SS.TcsGrpIntfSVC.dealAdcMasMemBiz", getInput(), false);

    }

}
