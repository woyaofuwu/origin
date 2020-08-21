
package com.asiainfo.veris.crm.order.soa.frame.bcf.crmlog;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;

public final class CrmLogCache extends AbstractReadOnlyCache
{
    // private final static Logger logger = Logger.getLogger(IvrLogCache.class);

    public class CrmOperCfg implements Serializable
    {
        public String pageName; // 页面名

        public String svcName; // 服务名

        public String matchKey; // 匹配KEY

        public String matchValue; // 匹配值

        public String opCode; // 业务编码

        public String opType; // 操作类型

        public String operTypeCode; // 操作类型编码，对应参数表TD_B_STAFFOPERTYPE

        public String operMod; // 操作模块：见参数表TD_S_STATIC/CRMOPERLOG_OPERMOD

        public String operType;// 操作类型：见参数表TD_S_STATIC/CRMOPERLOG_OPERTYPE

        public String operLevel; // 操作级别：见参数表TD_S_STATIC/CRMOPERLOG_OPERLEVEL

        public String sysTime; // 当前系统时间
    }

    @Override
    public Map<String, Object> loadData() throws Exception
    {
        Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT PAGE_NAME, SVC_NAME, MATCH_KEY, MATCH_VALUE, OP_CODE, OP_TYPE, OPER_TYPE_CODE, OPER_MOD, OPER_TYPE, OPER_LEVEL FROM TD_B_CRM_LOG T WHERE T.USE_TAG = 'U'";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                // 构造对象
                CrmOperCfg crmOperCfg = new CrmOperCfg();

                crmOperCfg.pageName = rs.getString("PAGE_NAME");
                crmOperCfg.svcName = rs.getString("SVC_NAME");
                crmOperCfg.matchKey = rs.getString("MATCH_KEY");
                crmOperCfg.matchValue = rs.getString("MATCH_VALUE");
                crmOperCfg.opCode = rs.getString("OP_CODE");
                crmOperCfg.opType = rs.getString("OP_TYPE");
                crmOperCfg.operTypeCode = rs.getString("OPER_TYPE_CODE");
                crmOperCfg.operMod = rs.getString("OPER_MOD");
                crmOperCfg.operType = rs.getString("OPER_TYPE");
                crmOperCfg.operLevel = rs.getString("OPER_LEVEL");

                // if (logger.isDebugEnabled())
                // {
                // StringBuilder sb = new StringBuilder(100);
                // sb.append("TRADE_TYPE_CODE=[").append(tradeTypeCode).append("] PARAM_TYPE=[").append(paramType).append("] PARAM_NAME=[").append(paramName).append("] PARAM_VALUE=[").append(paramValue).append("]");
                // logger.debug(sb);
                // }

                String key = CrmLog.getIvrkey(crmOperCfg.pageName, crmOperCfg.svcName);

                rtn.put(key.toString(), crmOperCfg);
            }

            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            if (conn != null)
            {
                conn.close();
            }
        }

        return rtn;
    }
}
