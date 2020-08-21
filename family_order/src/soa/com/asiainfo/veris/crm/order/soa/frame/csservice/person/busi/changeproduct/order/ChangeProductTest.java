
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class ChangeProductTest extends BizProcess
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
        input.put(BizConstants.SUBSYS_CODE, "saleserv");

        ChangeProductTest test = new ChangeProductTest();
        test.start(input);

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
        input.put("SERIAL_NUMBER", "15116370001");
        input.put("NEW_PRODUCT_ID", "31681210");
        // TODO new ChangeProductSVC().execute(input);
        return null;
    }
}
