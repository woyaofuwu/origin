
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.log;

import java.sql.Connection;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.DaoHelper;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.ConnMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class LogDAO
{

    public void CommonLog(IData param) throws Exception
    {

        // 传入参数的规范
        // 第1位，如果是输入参数，则为i，如果是输出参数，则为o
        // 第2位，如果是参数类型为number，则为n，如果参数类型为varchar,则为v，
        // 如果参数类型为date，则为d
        String[] inParam =
        { "iv_v_trade_eparchy_code", "iv_v_trade_city_code", "iv_v_trade_depart_id", "iv_v_trade_staff_id", "iv_v_CustId", "iv_v_OperType", "iv_v_LogId", "iv_v_LogType", "id_v_LogDate", "iv_v_Rsrvstr1", "iv_v_Rsrvstr2", "iv_v_Rsrvstr3",
                "iv_v_Rsrvstr4", "on_v_resultcode", "ov_v_resulterrinfo" };

        IData data = new DataMap();

        data.put("iv_v_trade_eparchy_code", CSBizBean.getVisit().getStaffEparchyCode());
        data.put("iv_v_trade_city_code", CSBizBean.getVisit().getCityCode());
        data.put("iv_v_trade_depart_id", CSBizBean.getVisit().getDepartId());
        data.put("iv_v_trade_staff_id", CSBizBean.getVisit().getStaffId());

        data.put("iv_v_CustId", param.get("CUST_ID"));
        data.put("iv_v_OperType", param.get("OPER_TYPE"));
        data.put("iv_v_LogId", param.get("LOG_ID"));
        data.put("iv_v_LogType", param.get("LOG_TYPE"));
        data.put("id_v_LogDate", param.get("LOG_DATE"));
        data.put("iv_v_Rsrvstr1", param.get("RSRV_STR1"));
        data.put("iv_v_Rsrvstr2", param.get("RSRV_STR2"));
        data.put("iv_v_Rsrvstr3", param.get("RSRV_STR3"));
        data.put("iv_v_Rsrvstr4", param.get("RSRV_STR4"));

        Connection conn = null;

        if ("CUST_GROUP".equals(param.getString("LOG_TYPE")))
        {
            // 集团客户需要连接到CG库
            conn = ConnMgr.getDBConn(Route.CONN_CRM_CG);
        }
        else
        {
            conn = ConnMgr.getDBConn();
        }

        DaoHelper.callProc(conn, "pk_cms_trade_log.Common_Log", inParam, data);

        String result_code = data.get("on_v_resultcode").toString();
        String result_info = data.getString("ov_v_resulterrinfo", "");

        if (!"0".equals(result_code))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, result_code, result_info);
        }
    }
}
