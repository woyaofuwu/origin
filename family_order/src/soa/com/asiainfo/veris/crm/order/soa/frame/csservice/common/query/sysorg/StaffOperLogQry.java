
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class StaffOperLogQry
{

    public static void getStaffOperLog(IData data) throws Exception
    {

        IData params = new DataMap();

        params.put("OPER_TYPE_CODE", IDataUtil.chkParam(data, "OPER_TYPE_CODE"));

        // 业务受理月份
        String ACCEPT_DATE = SysDateMgr.getSysDate("MMdd");

        // 操作对象的号码
        String SERIAL_NUMBER = IDataUtil.chkParam(data, "SERIAL_NUMBER");

        params.put("ACCEPT_DATE", ACCEPT_DATE);
        params.put("SERIAL_NUMBER", SERIAL_NUMBER);
        params.put("CLIENT_IP", data.getString("CLIENT_IP"));
        params.put("CLIENT_MAC", data.getString("CLIENT_MAC"));
        params.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
        params.put("OPER_TIME", SysDateMgr.getSysDate());
        params.put("TRADE_STAFF_ID", IDataUtil.chkParam(data, "TRADE_STAFF_ID"));
        params.put("TRADE_DEPART_ID", IDataUtil.chkParam(data, "TRADE_DEPART_ID"));
        params.put("TRADE_CITY_CODE", IDataUtil.chkParam(data, "TRADE_CITY_CODE"));
        params.put("TRADE_EPARCHY_CODE", IDataUtil.chkParam(data, "TRADE_EPARCHY_CODE"));
        params.put("REMARK", data.getString("REMARK"));
        params.put("RSRV_FEE1", data.getString("RSRV_FEE1"));
        params.put("RSRV_STR1", data.getString("RSRV_STR1"));

        Dao.insert("TF_B_STAFFOPERLOG", params, Route.CONN_LOG);
    }
}
