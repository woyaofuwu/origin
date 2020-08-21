package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.BizConstants;

import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dbconn.DBConnection;

public class MsisdnCache extends AbstractReadOnlyCache {
	 @Override
	    public Map<String, Object> loadData() throws Exception
	    {
	        Map<String, Object> rtn = new HashMap<String, Object>();

	        DBConnection conn = null;
	        Statement stmt = null;
	        ResultSet rs = null;
	        StringBuilder sBuilder = new StringBuilder();
	       
	        sBuilder.append("SELECT AREA_CODE,PROV_CODE,ASP,HOME_TYPE , BEGIN_MSISDN FROM TD_MSISDN  ");
	        sBuilder.append("where 1=1 ");
	        sBuilder.append("and AREA_CODE ='0898'");
	        sBuilder.append("and begin_time<= to_char(sysdate,'yyyymmddhh24miss') ");
	        sBuilder.append("and end_time >= to_char(sysdate,'yyyymmddhh24miss')  ");
	        sBuilder.append("AND end_msisdn >= to_char(TO_NUMBER( begin_msisdn)+9999) ");
	        
	        String sql =sBuilder.toString();
	        
	        try
	        {
	            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

	            stmt = conn.createStatement();
	            rs = stmt.executeQuery(sql);

	            while (rs.next())
	            {
	                String begin_msisdn = rs.getString("BEGIN_MSISDN");

	                IData rows = new DataMap();
	                //rows.put("begin_msisdn", rs.getString("begin_msisdn"));
	                rows.put("AREA_CODE", rs.getString("AREA_CODE"));
	                rows.put("PROV_CODE", rs.getString("PROV_CODE"));
	                rows.put("ASP", rs.getString("ASP"));
	                rows.put("HOME_TYPE", rs.getString("HOME_TYPE"));
	                
	                IDataset datas = new DatasetList();
	                datas.add(rows);
       
	                rtn.put(begin_msisdn, datas);
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
