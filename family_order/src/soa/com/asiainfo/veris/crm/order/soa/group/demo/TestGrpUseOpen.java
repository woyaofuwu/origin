
package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TestGrpUseOpen extends BizProcess
{

    public static void main(String[] args) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put(BizConstants.STAFF_ID, "SUPERUSR");
        inparam.put(BizConstants.STAFF_NAME, "SUPERUSR");
        inparam.put(BizConstants.SERIAL_NUMBER, "12553317377");
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
        inparam.put(BizConstants.IN_MODE_CODE, "2");
        inparam.put(BizConstants.REMOTE_ADDR, "192.168.97.146");
        inparam.put(BizConstants.SUBSYS_CODE, "groupserv");

        inparam.put("CUST_ID", "7400000016004402");
        inparam.put("PRODUCT_ID", "7800");
        inparam.put("SERIAL_NUMBER", "12553317377");
        inparam.put("ACCT_IS_ADD", false);

        // IData userInfo = new DataMap();
        // userInfo.put("USER_DIFF_CODE", "11");
        // userInfo.put("CONTRACT_ID", "7400000016004402");// 合同号
        // inparam.put("USER_INFO", userInfo);
        // 合户时需要传入ACCT_ID
        inparam.put("ACCT_ID", "1111012713370397");

        inparam.put("EFFECT_NOW", "true");

        IDataset res_infos = new DatasetList();
        IData res_info = new DataMap();
        res_info.put("MODIFY_TAG", "0");
        res_info.put("RES_TYPE_CODE", "G");
        res_info.put("RES_CODE", "12553317377");
        res_info.put("CHECKED", "true");
        res_info.put("DISABLED", "true");
        res_infos.add(res_info);

        // inparam.put("RES_INFO", res_infos);
        inparam.put("POST_INFO", new DatasetList());
        inparam.put("ASKPRINT_INFO", new DatasetList());
        inparam.put("GRP_PACKAGE_INFO", new DatasetList("[]"));

        IData payPlan = new DataMap();
        payPlan.put("MODIFY_TAG", "0");
        payPlan.put("PLAN_TYPE_CODE", "P");

        // inparam.put("PLAN_INFO", payPlan);

        IDataset element_infos = new DatasetList();
        IData element_info = new DataMap();
        element_info.put("INST_ID", "");
        element_info.put("START_DATE", "2014-03-13 17:04:29");
        element_info.put("ELEMENT_TYPE_CODE", "S");
        element_info.put("MODIFY_TAG", "0");
        element_info.put("PRODUCT_ID", "7800");
        element_info.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        element_info.put("ATTR_PARAM", "");
        element_info.put("PACKAGE_ID", "78000001");
        element_info.put("ELEMENT_ID", "755");

        element_infos.add(element_info);
        inparam.put("ELEMENT_INFO", element_infos);

        // 费用信息
        inparam.put("X_TRADE_FEESUB", null);
        inparam.put("X_TRADE_PAYMONEY", null);

        inparam.put("REMARK", "曹操");

        // 集团产品受理时的用户地州为交易地州

        TestGrpUseOpen testGrpUseOpen = new TestGrpUseOpen();
        testGrpUseOpen.setRouteId("0898");
        testGrpUseOpen.setGroup("saleserv");
        testGrpUseOpen.start(inparam);

    }

    @Override
    public void run() throws Exception
    {
        IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/saleserv/service", "CS.CreateGroupUserSvc.createGroupUser", getInput(), false);
    }
}
