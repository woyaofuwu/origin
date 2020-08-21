
package com.asiainfo.veris.crm.order.soa.group.demo.mvel;

import org.apache.log4j.Logger;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.GrpRegTradeData;

/**
 * 用于mvel测试,bizData为弱内型
 * 
 * @author 99
 */
public class TestMvel extends BizProcess
{
    private static final Logger logger = Logger.getLogger(TestMvel.class);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        IData input = new DataMap();

        // 上下文数据,即Visit里的信息
        input.put(BizConstants.STAFF_ID, "SUPERUSR");
        input.put(BizConstants.STAFF_NAME, "SUPERUSR");
        input.put(BizConstants.STAFF_EPARCHY_CODE, "0898");
        input.put(BizConstants.STAFF_EPARCHY_NAME, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_CODE, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_NAME, "0898");
        input.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");
        input.put(Route.USER_EPARCHY_CODE, "0898");
        input.put(BizConstants.DEPART_ID, "xxxx");
        input.put(BizConstants.DEPART_CODE, "xxxx");
        input.put(BizConstants.DEPART_NAME, "xxxx");
        input.put(BizConstants.CITY_CODE, "xxxx");
        input.put(BizConstants.CITY_NAME, "xxxx");
        input.put(BizConstants.PROVINCE_CODE, "xxxx");
        input.put(BizConstants.IN_MODE_CODE, "0");
        input.put(BizConstants.REMOTE_ADDR, "xxxx");
        input.put(BizConstants.SUBSYS_CODE, "saleserv");

        // 创建Process对象，并设置归属组用来控制数据库连接的路由
        TestMvel process = new TestMvel();
        process.setGroup("personserv");
        process.setTradeEparchyCode("0898");
        process.setUserEparchyCode("0898");
        process.setRouteId("0898");

        // 显示执行结果
        boolean result = process.start(input);
    }

    public void run() throws Exception
    {

        testMvel();
    }

    public void testMvel() throws Exception
    {

        String templateContent = "呵呵,@{ADD_DISCNT_NAME}哈哈,原来是: @{GRP_ACCEPT_MODE}";

        // String templateContent1 = "哈哈,原来是: @{GRP_ACCEPT_MODE}";

        IData aa = new DataMap();
        aa.put("TRADE_ID", "1114080104874003");
        aa.put("PROCESS_TAG_SET", "0000000000000000000010000000000000000000");
        aa.put("PRODUCT_ID", "天空飘来5个痣,那都不是事!");

        IDataset aaa99 = new DatasetList();
        IData aa99 = new DataMap();
        aa99.put("SERVICE_ID", "9000");
        aa99.put("MODIFY_TAG", "0");

        IData aa999 = new DataMap();
        aa999.put("SERVICE_ID", "9001");
        aa999.put("MODIFY_TAG", "0");

        aaa99.add(aa99);
        aaa99.add(aa999);

        IData aa2 = new DataMap();
        aa2.put("TF_B_TRADE_SVC", aaa99);
        aa2.put("TF_B_TRADE_DISCNT", new DatasetList());
        aa2.put("TF_B_TRADE", aa);

        GrpRegTradeData<BaseTradeData> tt = new GrpRegTradeData(aa);
        tt.setGrpRegTradeData(aa2);

        MVELMiscCache miscCache = CRMMVELMiscCache.getMacroCache();
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(miscCache);
        exector.prepare(tt);

        String result = exector.applyTemplate(templateContent);
        // String result1 = exector.applyTemplate(templateContent1);
        if (logger.isDebugEnabled())
        {

            logger.debug("测试的结果为: =================" + result);
            // logger.debug("测试的结果为: ================="+result1);
        }

    }

}
