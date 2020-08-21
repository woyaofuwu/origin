
package com.asiainfo.veris.crm.order.soa.frame.bcf.crmlog;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public final class AuditLogCache extends AbstractReadOnlyCache
{
    public class AuditOperCfg implements Serializable
    {
        public String pageName; // 页面名

        public String svcName; // 服务名

        public String operTypeCode;

        public String operTypeName;//

        public String sysTime; // 当前系统时间

        public String auditOperType; //

        public String auditOperTypeName; //

        public String auditOperSubType; //

        public String auditOperSubName; //

        public String auditInfoCode; //

        public String auditInfoName; //

        public String auditInfoLevel; //

        public String auditInfoLevelName; //

        public String auditRsrvstr2; //

        public String auditRsrvstr3; //

        public String auditRsrvstr4; //

        public String batchTag;

        public String fromSubSys; //

    }

    @Override
    public Map<String, Object> loadData() throws Exception
    {
        Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT OPER_TYPE_CODE, OPER_TYPE_NAME, OBJECTID, AUDIT_OPER_TYPE, AUDIT_OPER_TYPE_NAME, AUDIT_OPER_SUB_TYPE, AUDIT_OPER_SUB_NAME, AUDIT_INFO_CODE, AUDIT_INFO_NAME, AUDIT_INFO_LEVEL, AUDIT_INFO_LEVEL_NAME, RSRV_STR2, RSRV_STR3, RSRV_STR4, BATCH_TAG, FROM_SUB_SYS FROM TD_B_AUDITOPERTYPE T WHERE T.ACT_TAG = '1'";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                // 构造对象
                AuditOperCfg auditOperCfg = new AuditOperCfg();

                auditOperCfg.pageName = rs.getString("OBJECTID");
                auditOperCfg.svcName = rs.getString("RSRV_STR4");
                auditOperCfg.operTypeCode = rs.getString("OPER_TYPE_CODE");
                auditOperCfg.operTypeName = rs.getString("OPER_TYPE_NAME");
                auditOperCfg.auditOperType = rs.getString("AUDIT_OPER_TYPE");
                auditOperCfg.auditOperTypeName = rs.getString("AUDIT_OPER_TYPE_NAME");
                auditOperCfg.auditOperSubType = rs.getString("AUDIT_OPER_SUB_TYPE");
                auditOperCfg.auditOperSubName = rs.getString("AUDIT_OPER_SUB_NAME");
                auditOperCfg.auditInfoCode = rs.getString("AUDIT_INFO_CODE");
                auditOperCfg.auditInfoName = rs.getString("AUDIT_INFO_NAME");
                auditOperCfg.auditInfoLevel = rs.getString("AUDIT_INFO_LEVEL");
                auditOperCfg.auditInfoLevelName = rs.getString("AUDIT_INFO_LEVEL_NAME");
                auditOperCfg.auditRsrvstr2 = rs.getString("RSRV_STR2");
                auditOperCfg.auditRsrvstr3 = rs.getString("RSRV_STR3");
                auditOperCfg.batchTag = rs.getString("BATCH_TAG");
                auditOperCfg.fromSubSys = rs.getString("FROM_SUB_SYS");

                String key = CrmLog.getIvrkey(auditOperCfg.pageName, auditOperCfg.svcName);

                rtn.put(key.toString(), auditOperCfg);
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
