
package com.asiainfo.veris.crm.order.soa.frame.bcf.demotest;

import com.ailk.biz.util.ErrorCodeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class LySvc
{
    public static void breLog(IData input) throws Exception
    {
        input = new DataMap();

        input.put("RULE_BIZ_TYPE_CODE", "TradeCheckAfter");
        input.put("RULE_BIZ_KIND_CODE", "TradeCheckSuperLimit");

        CSAppCall.call("CS.bre4UniteInterface", input);
    }

    public static void chkLog(IData input) throws Exception
    {
        input.put("TRADE_TYPE_CODE", "2410");

        CSAppCall.call("CS.chkGrpUserOpen", input);
    }

    public static void exptest(IData input) throws Exception
    {
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        String message = ErrorCodeUtil.trans(inModeCode, "IGM_10", "lyexp");
    }

    public static void finish(IData input) throws Exception
    {
        input.put("TRADE_ID", "3113110448478682");
        input.put("ACCEPT_MONTH", "11");

        IDataset ids = CSAppCall.call("CS.TradeFinishSVC.finish", input);
    }

    public static void fuzzy(IData input) throws Exception
    {
        // input.put("CUST_ID", "3409040310100101");
        // IDataset ids = CSAppCall.call("CS.UcaInfoQrySVC.qryCustomerInfoByCustId", input);

        input.put("TRADE_TYPE_CODE", "110");
        input.put("SERIAL_NUMBER", "13974858745");
        IDataset ids = CSAppCall.call("CS.GetInfosSVC.getUCAInfos", input);
    }

    public static void sendpf(IData input) throws Exception
    {
        input.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());

        input.put("ORDER_ID", "3193110585936323");
        input.put("ACCEPT_MONTH", "11");
        input.put("CANCEL_TAG", "0");

        IDataset ids = CSAppCall.call("CS.TradePfSVC.sendPf", input);
    }
}
