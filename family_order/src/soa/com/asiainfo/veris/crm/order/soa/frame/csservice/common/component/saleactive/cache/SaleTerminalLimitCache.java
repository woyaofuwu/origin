
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

public class SaleTerminalLimitCache extends AbstractReadOnlyCache
{

    @Override
    public Map<String, Object> loadData() throws Exception
    {
        Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "select D.PRODUCT_ID,D.PACKAGE_ID,D.TERMINAL_TYPE_CODE, D.TERMINAL_MODE_CODE,D.EPARCHY_CODE from TD_B_SALE_TERMINAL_LIMIT D WHERE D.STATE = '1' ";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            List<SaleTerminalLimitObject> terminalLimitList = new ArrayList<SaleTerminalLimitObject>();

            while (rs.next())
            {
                SaleTerminalLimitObject saleTermianlObj = new SaleTerminalLimitObject();

                saleTermianlObj.setProductId(rs.getString("PRODUCT_ID"));
                saleTermianlObj.setPackageId(rs.getString("PACKAGE_ID"));
                saleTermianlObj.setTerminalTypeCode(rs.getString("TERMINAL_TYPE_CODE"));
                saleTermianlObj.setTerminalModeCode(rs.getString("TERMINAL_MODE_CODE"));
                saleTermianlObj.setEparchyCode(rs.getString("EPARCHY_CODE"));

                terminalLimitList.add(saleTermianlObj);
            }

            rtn.put(SaleTerminalLimitObject.SLAE_TERMINAL_LIMIT_CHCHE_KEY, terminalLimitList);

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
