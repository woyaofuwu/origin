
package com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;

public final class TradeCtrlCache extends AbstractReadOnlyCache
{
    // private final static Logger logger = Logger.getLogger(TradeCtrlCache.class);
    /**
     * 获取缓存装载数据
     */
    @Override
    public Map<String, Object> loadData() throws Exception
    {
        Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT TRADE_TYPE_CODE, PARAM_NAME, PARAM_VALUE, PARAM_TYPE FROM TD_S_TRADECTRL T WHERE SYSDATE BETWEEN T.START_DATE AND T.END_DATE";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                String tradeTypeCode = rs.getString("TRADE_TYPE_CODE");
                String paramName = rs.getString("PARAM_NAME");
                String paramValue = rs.getString("PARAM_VALUE");
                char paramType = rs.getString("PARAM_TYPE").charAt(0);

                Object obj = null;

                if ('S' == paramType)
                {
                    obj = paramValue;
                }
                else if ('B' == paramType)
                {
                    obj = Boolean.valueOf(Boolean.parseBoolean(paramValue));
                }
                else if ('I' == paramType)
                {
                    obj = Integer.valueOf(Integer.parseInt(paramValue));
                }
                else
                {
                    obj = "";
                }

                rtn.put(tradeTypeCode + paramName, obj);
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
