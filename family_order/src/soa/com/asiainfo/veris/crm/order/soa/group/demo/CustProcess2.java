
package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 接口测试文件demo
 * 
 * @author huyong
 */
public class CustProcess2 extends BizProcess
{

    public static void main(String[] args) throws Exception
    {
        IData input = new DataMap();
        input.put(BizConstants.STAFF_ID, "SUPERUSR");
        input.put(BizConstants.STAFF_NAME, "SUPERUSR");
        input.put(BizConstants.SERIAL_NUMBER, "121212");
        input.put(BizConstants.STAFF_EPARCHY_CODE, "0731");
        input.put(BizConstants.STAFF_EPARCHY_NAME, "0731");
        input.put(BizConstants.LOGIN_EPARCHY_CODE, "0731");
        input.put(BizConstants.LOGIN_EPARCHY_NAME, "0731");
        input.put(BizConstants.ROUTE_EPARCHY_CODE, "0731");
        input.put(BizConstants.DEPART_ID, "00000");
        input.put(BizConstants.DEPART_CODE, "00000");
        input.put(BizConstants.DEPART_NAME, "");
        input.put(BizConstants.CITY_CODE, "xxxx");
        input.put(BizConstants.CITY_NAME, "xxxx");
        input.put(BizConstants.PROVINCE_CODE, "xxxx");
        input.put(BizConstants.IN_MODE_CODE, "xxxx");
        input.put(BizConstants.REMOTE_ADDR, "xxxx");
        input.put(BizConstants.SUBSYS_CODE, "saleserv");

        // input.put("SERIAL_NUMBER", "13407310011");//成员手机号码
        // input.put("EPARCHY_CODE", "0731");//成员手机号码
        // //input.put("USER_ID", "9193060424137391");//集团产品用户ID
        // input.put("REMARK", "测试接口成员退订");//集团产品用户ID
        // //input.put("SERIAL_NUMBER_A", "31XG6340");//成员手机号码
        // input.put("IN_MODE_CODE", "3");//成员手机号码

        // 1. SERIAL_NUMBER 用户服务号码
        // 2. GROUP_ID 集团编码
        // 3. PRODUCT_ID 产品编码
        // 4. MODIFY_TAG 操作标识 0-订购 1-退订
        // 5. REMOVE_TAG 0-正常用户
        input.put("SERIAL_NUMBER", "13548757851");// 成员手机号码

        input.put("EPARCHY_CODE", "0731");// 成员手机号码
        input.put("USER_ID", "9193053124131791");// 集团产品用户ID

        input.put("REMARK", "测试接口成员退订");// 集团产品用户ID

        input.put("IN_MODE_CODE", "0");// 集团产品用户ID

        CustProcess2 test = new CustProcess2();
        test.setRouteId("0731");
        test.setGroup("saleserv");
        test.start(input);
    }

    @Override
    public void run() throws Exception
    {
        CSAppCall.call("http://127.0.0.1:8080/saleserv/service", "CS.DestroyGroupMemberSvc.destroyGroupMember", getInput(), false);

    }
}
