
package com.asiainfo.veris.crm.order.soa.frame.bcf.touch;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;

public final class CustTouchCache extends AbstractReadOnlyCache
{
    // private final static Logger logger = Logger.getLogger(CustTouchCache.class);

    /**
     * 装载客户接触数据
     */
    @Override
    public Map<String, Object> loadData() throws Exception
    {
        Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT SVC_NAME FROM TD_B_CUST_TOUCH T WHERE T.USE_TAG = 'U'";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                String svcNmae = rs.getString("SVC_NAME").trim();
                rtn.put(svcNmae, 1);
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
