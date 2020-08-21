
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.cache;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;

public class SaleTradeLimitCache extends AbstractReadOnlyCache
{
    @Override
    public Map<String, Object> loadData() throws Exception
    {
        Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "select D.CAMPN_TYPE,D.PRODUCT_ID,D.PACKAGE_ID,D.LIMIT_TAG,D.LIMIT_TYPE,D.LIMIT_CODE, D.LIMIT_MSG from TD_B_SALE_TRADE_LIMIT D where D.STATE = '1' ";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            List<SaleTradeLimitObj> tradeLimitList = new ArrayList<SaleTradeLimitObj>();

            while (rs.next())
            {
                SaleTradeLimitObj tradeLimitObj = new SaleTradeLimitObj();

                tradeLimitObj.setCampnType(rs.getString("CAMPN_TYPE"));
                tradeLimitObj.setProductId(rs.getString("PRODUCT_ID"));
                tradeLimitObj.setPackageId(rs.getString("PACKAGE_ID"));
                tradeLimitObj.setLimitTag(rs.getString("LIMIT_TAG"));
                tradeLimitObj.setLimitType(rs.getString("LIMIT_TYPE"));
                tradeLimitObj.setLimitCode(rs.getString("LIMIT_CODE"));
                tradeLimitObj.setLimitMsg(rs.getString("LIMIT_MSG"));

                tradeLimitList.add(tradeLimitObj);
            }

            rtn.put(SaleTradeLimitObj.SLAE_TRADE_LIMIT_CHCHE_KEY, tradeLimitList);

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
