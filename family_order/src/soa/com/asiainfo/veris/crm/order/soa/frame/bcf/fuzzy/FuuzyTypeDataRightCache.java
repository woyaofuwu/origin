package com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.database.dbconn.DBConnection;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzyCache.FuzzyRule;

public class FuuzyTypeDataRightCache extends AbstractReadOnlyCache{
	
	@Override
	public Map<String, Object> loadData() throws Exception {
		Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT FUZZY_TYPE, RIGHT_CODE FROM TD_B_FUZZYTYPE_DATARIGHT T";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                String fuzzyType = rs.getString("FUZZY_TYPE");
                String rightCode = rs.getString("RIGHT_CODE");

                rtn.put(fuzzyType, rightCode);
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
