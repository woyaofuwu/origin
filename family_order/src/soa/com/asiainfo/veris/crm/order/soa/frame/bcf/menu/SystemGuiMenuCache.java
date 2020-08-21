package com.asiainfo.veris.crm.order.soa.frame.bcf.menu;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;

public class SystemGuiMenuCache extends AbstractReadOnlyCache{
	
	@Override
	public Map<String, Object> loadData() throws Exception {
		Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT MENU_ID, MENU_TEXT FROM TD_B_SYSTEMGUIMENU T";

        try
        {
            conn = new DBConnection(BizConstants.DBROUTE_SYS_NAME, false, false);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                String menuId = rs.getString("MENU_ID");
                String menuText = rs.getString("MENU_TEXT");

                rtn.put(menuId, menuText);
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

