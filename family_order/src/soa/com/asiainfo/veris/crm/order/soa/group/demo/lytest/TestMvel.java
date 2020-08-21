
package com.asiainfo.veris.crm.order.soa.group.demo.lytest;

import org.apache.log4j.Logger;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.GrpRegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;

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

    public void testMvel2() throws Exception{
    	IData retData = new DataMap();
    	 // 获取模板信息
        IDataset tradeReceiptList = TradeReceiptInfoQry.getReceiptInfoByPk("3035", "", "", "", "", null);

        if (IDataUtil.isEmpty(tradeReceiptList))
        {
            return ;
        }

        // 获取模板TEMPLATE_ID
        String templateId = tradeReceiptList.getData(0).getString("TEMPLATE_ID");

        IData templatData = TemplateQry.qryTemplateContentByTempateId(templateId);

        if (IDataUtil.isEmpty(templatData))
        {
            return ;
        }

        // 解析模板数据
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

//        exector.prepare(regTradeData);

        for (int i = 1; i <= 5; i++)
        {
            String contentStr = templatData.getString("TEMPLATE_CONTENT" + i, "");
            if (StringUtils.isNotEmpty(contentStr))
            {
                retData.put("RECEIPT_INFO" + i, exector.applyTemplate(contentStr));
            }
            else
            {
                retData.put("RECEIPT_INFO" + i, "");
            }
        }
    }
    
    public void testMvel() throws Exception
    {

//        String templateContent = "呵呵,@{ADD_DISCNT_NAME}哈哈,原来是: @{GRP_ACCEPT_MODE}";
        String templateContent = "~~      业务类型：@{TRADE_TYPE_NAME}      受理方式：@{GRP_ACCEPT_MODE}~~      VPMN编码：@{GRP_SERIAL_NUMBER}~~      VPMN名称：@{CUST_GROUP_NAME}~~      短号码：@{ADD_RES_CODE}~~      新增优惠：@{ADD_DISCNT}~~      温馨提示：集团V网的管理权归属集团单位";
        // String templateContent1 = "哈哈,原来是: @{GRP_ACCEPT_MODE}";

        IData aa = new DataMap();
        aa.put("TRADE_ID", "1114080104874003");
        aa.put("PROCESS_TAG_SET", "0000000000000000000010000000000000000000");
        aa.put("PRODUCT_ID", "天空飘来5个痣,那都不是事!");
        aa.put("TRADE_TYPE_CODE", "3034");

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
        aa2.put("TF_B_TRADE_DISCNT", new DatasetList("[{\"RSRV_STR2\":\"\",\"RSRV_STR1\":\"\",\"RSRV_STR4\":\"\",\"PRODUCT_ID\":\"-1\",\"MODIFY_TAG\":\"0\",\"RSRV_STR3\":\"\",\"RSRV_STR5\":\"\",\"END_DATE\":\"2050-12-31 23:59:59\",\"RELATION_TYPE_CODE\":\"20\",\"REMARK\":\"\",\"USER_ID_A\":\"1108032607900563\",\"IS_NEED_PF\":\"\",\"RSRV_TAG3\":\"\",\"RSRV_TAG2\":\"\",\"UPDATE_DEPART_ID\":\"36601\",\"RSRV_TAG1\":\"1\",\"PACKAGE_ID\":\"-1\",\"TRADE_ID\":\"1117041700275214\",\"OFFER_TYPE\":\"D\",\"ACCEPT_MONTH\":\"04\",\"INST_ID\":\"1117041731885315\",\"OPER_CODE\":\"\",\"DISCNT_CODE\":\"255\",\"USER_ID\":\"1109050824650720\",\"UPDATE_TIME\":\"2017-04-17 17:42:10\",\"SPEC_TAG\":\"2\",\"RSRV_NUM5\":\"\",\"START_DATE\":\"2017-04-17 17:42:10\",\"RSRV_NUM4\":\"\",\"CAMPN_ID\":\"\",\"RSRV_DATE2\":\"\",\"RSRV_DATE3\":\"\",\"RSRV_DATE1\":\"\",\"RSRV_NUM1\":\"0\",\"UPDATE_STAFF_ID\":\"SUPERUSR\",\"RSRV_NUM3\":\"0\",\"RSRV_NUM2\":\"0\"}]"));
        aa2.put("TF_B_TRADE", aa); 
        IDataset offerRel = new DatasetList("[{\"ACCEPT_MONTH\":\"04\",\"INST_ID\":\"1117041731885323\",\"REL_OFFER_INS_ID\":\"1117041731885313\",\"GROUP_ID\":\"80000101\",\"USER_ID\":\"1109050824650720\",\"MODIFY_TAG\":\"0\",\"REL_TYPE\":\"C\",\"END_DATE\":\"2050-12-31 23:59:59\",\"REMARK\":\"\",\"UPDATE_TIME\":\"2017-04-17 17:42:10\",\"OFFER_INS_ID\":\"1117041731885316\",\"REL_OFFER_CODE\":\"861\",\"START_DATE\":\"2017-04-17 17:42:10\",\"UPDATE_DEPART_ID\":\"36601\",\"REL_USER_ID\":\"1109050824650720\",\"OFFER_CODE\":\"800001\",\"REL_OFFER_TYPE\":\"S\",\"TRADE_ID\":\"1117041700275214\",\"UPDATE_STAFF_ID\":\"SUPERUSR\",\"OFFER_TYPE\":\"P\"},{\"ACCEPT_MONTH\":\"04\",\"INST_ID\":\"1117041731885324\",\"REL_OFFER_INS_ID\":\"1117041731885314\",\"GROUP_ID\":\"0\",\"USER_ID\":\"1109050824650720\",\"MODIFY_TAG\":\"0\",\"REL_TYPE\":\"C\",\"END_DATE\":\"2050-12-31 23:59:59\",\"REMARK\":\"\",\"UPDATE_TIME\":\"2017-04-17 17:42:10\",\"OFFER_INS_ID\":\"1117041731885316\",\"REL_OFFER_CODE\":\"860\",\"START_DATE\":\"2017-04-17 17:42:10\",\"UPDATE_DEPART_ID\":\"36601\",\"REL_USER_ID\":\"1109050824650720\",\"OFFER_CODE\":\"800001\",\"REL_OFFER_TYPE\":\"S\",\"TRADE_ID\":\"1117041700275214\",\"UPDATE_STAFF_ID\":\"SUPERUSR\",\"OFFER_TYPE\":\"P\"}]");
        aa2.put("TF_B_TRADE_OFFER_REL", offerRel);
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
