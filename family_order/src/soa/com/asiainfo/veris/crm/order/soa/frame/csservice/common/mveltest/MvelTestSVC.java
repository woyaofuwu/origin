
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.mveltest;

import com.ailk.biz.BizConstants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegOrderData;

public class MvelTestSVC extends CSBizService
{

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        IData input = new DataMap();
        input.put(BizConstants.STAFF_ID, "SUPERUSR");
        input.put(BizConstants.STAFF_NAME, "SUPERUSR");
        input.put(BizConstants.SERIAL_NUMBER, "15116370001");
        input.put(BizConstants.STAFF_EPARCHY_CODE, Route.getCrmDefaultDb());
        input.put(BizConstants.STAFF_EPARCHY_NAME, Route.getCrmDefaultDb());
        input.put(BizConstants.LOGIN_EPARCHY_CODE, Route.getCrmDefaultDb());
        input.put(BizConstants.LOGIN_EPARCHY_NAME, Route.getCrmDefaultDb());
        input.put(BizConstants.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
        input.put(BizConstants.DEPART_ID, "00000");
        input.put(BizConstants.DEPART_CODE, "00000");
        input.put(BizConstants.DEPART_NAME, "00000");
        input.put(BizConstants.CITY_CODE, "0000");
        input.put(BizConstants.CITY_NAME, "0000");
        input.put(BizConstants.PROVINCE_CODE, "HNAN");
        input.put(BizConstants.IN_MODE_CODE, "0");
        input.put(BizConstants.REMOTE_ADDR, "0000");
        input.put(BizConstants.SUBSYS_CODE, "personserv");

        MvelTestSVC test = new MvelTestSVC();
        test.run(input);

        // String s = CSBaseConst.TRADE_SUCCESSFUL;
        // BizContext ctx = null;

        // bd.setStaffId("SUPERUSR");
        // bd.setDeptId("00000");
        // bd.setEpachyId("0731");
        // bd.setLoginEpachyId("0731");
        // bd.setCityId("0731");
        // ctx.setContext(bd);
        // IData param = new DataMap();
        // param.put("SERIAL_NUMBER", "15116410926");
        // param.put("SERVICE_ID", "98001901");
        // param.put("OPER_CODE", "01");
        // AppCtx.setParameter( "98001901_ATTR_CODE", "302");
        // AppCtx.setParameter( "98001901_ATTR_VALUE", "1");
        // PlatOrderBaseBean base = new PlatOrderBaseBean();
        // base.acceptOrder( param);
        // ctx.cleanupConnections();
    }

    public IDataset run(IData input) throws Exception
    {
        input.put("ORDER_ID", "3113083128671209");
        input.put("TEMPLATE_CONTENT", "尊敬的@{isExist('31511531','0')}");
        CSAppCall.call("CS.MvelTestSVC.testMvel", input);
        return null;
    }

    public IDataset testMvel(IData param) throws Exception
    {
        String orderId = param.getString("ORDER_ID");
        String templateContent = param.getString("TEMPLATE_CONTENT");
        RegOrderData regOrder = new RegOrderData(orderId);
        MVELMiscCache miscCache = CRMMVELMiscCache.getMacroCache();
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(miscCache);
        exector.prepare(regOrder.getMainRegData(), regOrder);
        String result = exector.applyTemplate(templateContent);
        IDataset results = new DatasetList();
        IData map = new DataMap();
        map.put("RESULT", result);
        results.add(map);
        return results;
    }
}
