
package com.asiainfo.veris.crm.order.soa.group.demo.lytest;

import org.apache.log4j.Logger;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.group.demo.TestProcess;

public class TestInterfaceSvc extends BizProcess
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        IData input = new DataMap();
        if (1 == 2)
        {
            // 本月订购次数 SS.QcsGrpIntfSVC.getAddCountMonth
            input.put("PRODUCT_ID", "8000"); // 集团产品ID
            input.put("SERIAL_NUMBER", "152089170091"); // 成员号码 15208917009 13976835517

            // 本月订购次数 SS.TcsGrpIntfSVC.processGrpMemberInfo
            input.put("SERIAL_NUMBER_A", "055512211"); // 集团SERIAL_NUMBER
            input.put("SERIAL_NUMBER", "15208917009"); // 成员号码 15208917009 13976835517
            input.put("X_CUST_TYPE", "SHORT_MESSAGE_PORTAL");
            input.put("MODIFY_TAG", "0");
            input.put("PRODUCT_ID", "6200"); // 工号id

            // vpmn信息查询 外围接口 SS.QcsGrpIntfSVC.getUserVpnInfo 测试：V0SJ003989 开发：V0SJ001328
            input.put("X_GETMODE", "0"); // X_GETMODE:0 取默认vpn信息; =1 取成员个性vpn信息
            input.put("SERIAL_NUMBER_A", "V0SJ001328");
            input.put("SERIAL_NUMBER", "13907683788");

            // vpmn信息查询 短信营业厅 SS.QcsGrpIntfSVC.getUserVpnInfo2ShortMes
            input.put("SERIAL_NUMBER_B", "13907683788");

            // 成员长短号互查 SS.QcsGrpIntfSVC.getRelaAndVpnInfoBySnOrShortcode
            String type = "2";
            if ("0".equals(type))// 成员长号
            {
                input.put("SERIAL_NUMBER", "15208917009");
                input.put("PARA_CODE1", "0");
                input.put("PARA_CODE2", "15208917009");
            }
            else if ("1".equals(type))// vpn_no和短号
            {
                input.put("PARA_CODE1", "1");
                input.put("PARA_CODE2", "V0SJ001328");
                input.put("PARA_CODE3", "6707");
            }
            else if ("2".equals(type))// vpn_name和短号
            {
                input.put("PARA_CODE1", "2");
                input.put("PARA_CODE2", "1106"); // 1106081800908174
                input.put("PARA_CODE3", "6707");
            }
            input.put("X_SUBTRANS_CODE", "CommQuery");

            // 集团成员关系查询 SS.QcsGrpIntfSVC.getRelaInfoBySnaSnbRelatypecode V0SJ001328 15208917009
            input.put("SERIAL_NUMBER_A", "V0SJ001328");
            input.put("SERIAL_NUMBER_B", "15208917009");
            input.put("RELATION_TYPE_CODE", "20");

            // 集团产品优惠查询 SS.QcsGrpIntfSVC.getProductMebDiscntByProductIdAndStaffId
            input.put("PRODUCT_ID", "8000");

            // 自动生成短号 SS.QcsGrpIntfSVC.getAutoGenShortCode V0SJ001328 15208917009
            input.put("SERIAL_NUMBER_A", "V0SJ001328");
            input.put("SERIAL_NUMBER", "15208917009");
            input.put("EPARCHY_CODE", "0898");

            // 短号校验 SS.QcsGrpIntfSVC.getValidShortCode V0SJ001328 6707
            input.put("SERIAL_NUMBER_A", "V0SJ001328");
            input.put("SHORT_CODE", "6707");
            input.put("EPARCHY_CODE", "0898");

            // 统一付费关系用户查询 SS.QcsGrpIntfSVC.getQryUsrSpePay V0SJ001328 6707
            input.put("PARA_CODE", "1"); // 0-用户手机号码 1-vpmn编号和用户短号码
            input.put("PARA_CODE1", "V0SJ001328"); // VPMN编号
            input.put("PARA_CODE2", "6707"); // 成员长号或短号

            // 本月订购次数查询 SS.QcsGrpIntfSVC.getAddCountMonth
            input.put("PRODUCT_ID", "8000"); // 集团产品编码
            input.put("SERIAL_NUMBER", "15208917009"); // 成员长号

            // 得到角色编码查询 SS.QcsGrpIntfSVC.getRoleCodeInfo
            input.put("PRODUCT_ID", "8000"); // 集团产品编码

            // 查询集团产品编码和成员优惠编码 SS.QcsGrpIntfSVC.GetDiscntCodeBySn
            input.put("SERIAL_NUMBER", "13807531392"); // 成员手机号码
            input.put("SERIAL_NUMBER_IN", "15208917009"); // 某V网的成员号码
            input.put(Route.ROUTE_EPARCHY_CODE, "0898"); // 路由地州

            // 查询判断用户是否VPMN用户并且返回本月办理VPMN成员新增业务的次数 SS.QcsGrpIntfSVC.getCreateVpnCountMonth
            input.put("SERIAL_NUMBER", "13807531392"); // 成员手机号码

            // 关于增加办理集团V网业务短信指令的需求,获取集团V网名称和集团订购优惠编码 SS.QcsGrpIntfSVC.GetVpnNameGrpPackageBySn
            input.put("SERIAL_NUMBER", "13807531392"); // 成员手机号码
            input.put("SERIAL_NUMBER_IN", "15208917009"); // 某V网的成员号码

            // 查询VPMN编码和VPMN名称 SS.QcsGrpIntfSVC.GetVpnNoBySn
            input.put("SERIAL_NUMBER", "13807531392"); // 成员手机号码
            input.put("SERIAL_NUMBER_IN", "15208917009"); // 某V网的成员号码
        }
        else if (2 == 3)
        {// TcsGrpIntfSVC
            // 集团成员优惠变更处理接口 给统一查询退订使用 SS.TcsGrpIntfSVC.processGrpChgMemDisct
            input.put("PRODUCT_ID", "8000");
            input.put("MODIFY_TAG", "2");
            input.put("GROUP_ID", "8980727777");
            input.put("DISCNT_CODE", "1393");// 优惠编码
            input.put("OPER_TYPE", "2");// 操作类型：1.优惠删除 2.优惠新增
            input.put("EFFECT_TIME", "0");// 生效类型：0.立即生效 1.下月生效
            input.put("SERIAL_NUMBER", "15208917009");

            // 集团彩铃成员处理 给短厅使用 SS.TcsGrpIntfSVC.processGrpCLRMem
            input.put("X_CUST_TYPE", "SHORT_MESSAGE_PORTAL");
            input.put("MODIFY_TAG", "0");
            input.put("SERIAL_NUMBER_A", "00755500792");
            input.put("SERIAL_NUMBER", "15208917009");

            // 集团成员退订处理接口 给统一查询退订使用 SS.TcsGrpIntfSVC.processGrpMemCancel
            input.put("MODIFY_TAG", "1");
            input.put("PRODUCT_ID", "8000");
            input.put("GROUP_ID", "8980727777");
            input.put("SERIAL_NUMBER", "15208917009");
         // 统一付费关系用户查询 SS.QcsGrpIntfSVC.getQryUsrSpePay V0SJ001328 6707
            input.put("PARA_CODE", "1"); // 0-用户手机号码 1-vpmn编号和用户短号码
            input.put("PARA_CODE1", "V0SJ001261"); // VPMN编号
            input.put("PARA_CODE2", "6268"); // 成员长号或短号
         // vpmn成员短号修改 SS.ChangeVpmnShortCodeSVC.crtTrade   
            input.put("X_SUBTRANS_CODE", "ProcessGrpMem");
            input.put("PARA_CODE3", "67433");  
            input.put("SHORT_CODE", "6268");  
            input.put("PRODUCT_ID", "8000");  
            input.put("SERIAL_NUMBER", "13518050493"); // VPMN编号
            input.put("SERIAL_NUMBER_A", "V0SJ004535"); // VPMN编号
            input.put("EPARCHY_CODE", "0898"); // 成员长号或短号 
            
         // 移动总机集团注销 SS.DestroySuperTeleGroupUserSVC.crtOrder
            input.put("PRODUCT_ID", "6100");
            input.put("USER_ID", "1107060502410184"); 
        }
        else if(1==2){
        	 // esop ITF_EOS_QcsGrpBusi   
            input.put("OPER_CODE", "13");
            input.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");  
            input.put("X_SUBTRANS_CODE", "GetEosInfo");  
            input.put("NODE_ID", "bossOpen");  
//            input.put("SERIAL_NUMBER", "13518050493");  
//            input.put("SERIAL_NUMBER_A", "V0SJ004535");  
//            input.put("EPARCHY_CODE", "0898");  
            
//            {OPER_CODE=["13"], X_TRANS_CODE=["ITF_EOS_QcsGrpBusi"], SUB_IBSYSID=[""], index=[""],
//            IN_MODE_CODE=["0"], X_SUBTRANS_CODE=["GetEosInfo"], TRADE_EPARCHY_CODE=["0898"],
//            ROUTE_EPARCHY_CODE=["cen"], TRADE_CITY_CODE=["HNSJ"], IBSYSID=["201703140017"],
//            NODE_ID=["bossOpen"], PROVINCE_CODE=["HAIN"], TRADE_STAFF_ID=["SUPERUSR"], 
//            TRADE_DEPART_ID=["36601"]}
        }else if(2==3){
        	//账务接口查用户是否欠款  http://10.200.138.14:10000/service", "AM_CRM_QueryRealFee
        	input.put("USER_ID", args[0]);
        }
        else
        { // 真正执行区
             
        	
        	// 移动总机集团注销 SS.DestroySuperTeleGroupUserSVC.crtOrder
            input.put("PRODUCT_ID", "6100");
            input.put("USER_ID", "1107060502410184"); 
        }
        // 公共参数
        // input.put("TRADE_STAFF_ID", "SUPERUSR"); // 工号id
        // input.put("TRADE_DEPART_ID", "36601"); // 工号部门id
        // input.put("TRADE_EPARCHY_CODE", "0898"); // 员工归属地
        // input.put("TRADE_CITY_CODE", "HNSJ"); // 工号部门id
        // input.put("IN_MODE_CODE", "2"); // 5短信平台
        //
        
        input.put(BizConstants.IN_MODE_CODE, "5"); // 5短信平台
        input.put(BizConstants.STAFF_ID, "SUPERUSR");
        input.put(BizConstants.STAFF_NAME, "SUPERUSR");
        input.put(BizConstants.SERIAL_NUMBER, "12553317377");
        input.put(BizConstants.STAFF_EPARCHY_CODE, "0898");
        input.put(BizConstants.STAFF_EPARCHY_NAME, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_CODE, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_NAME, "0898");
        input.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");
        input.put(BizConstants.DEPART_ID, "36601");
        input.put(BizConstants.DEPART_CODE, "HNSJ0000");
        input.put(BizConstants.DEPART_NAME, "移动省公司");
        input.put(BizConstants.CITY_CODE, "HNSJ");
        input.put(BizConstants.CITY_NAME, "省局");
        input.put(BizConstants.PROVINCE_CODE, "xxxx");
        input.put(BizConstants.REMOTE_ADDR, "10.199.51.81");
        input.put(BizConstants.SUBSYS_CODE, "groupserv"); 

        TestInterfaceSvc test = new TestInterfaceSvc();
        test.setRouteId("0898");
        test.setGroup("saleserv"); // saleserv
        test.start(input);
    }

    private Logger log = Logger.getLogger(TestProcess.class);

    @Override
    public void run() throws Exception
    {
        // IDataset ds = CSAppCall.call("http://127.0.0.1:8080/groupserv/service", "SS.QcsGrpIntfSVC.getQryUsrSpePay",
        // getInput(), false);
//        IDataset ds = CSAppCall.call("http://10.200.138.14:10000/service", "AM_CRM_QueryRealFee", getInput(), false);
    	  IDataset ds = CSAppCall.call("http://127.0.0.1:8080/order/service", "SS.DestroySuperTeleGroupUserSVC.crtOrder", getInput(), false);
//    	  IDataset ds = CSAppCall.call("http://10.200.130.84:8090/grpbiz_esop/httptran/CrmService", "ITF_EOS_QcsGrpBusi", getInput(), false);
    }
}
