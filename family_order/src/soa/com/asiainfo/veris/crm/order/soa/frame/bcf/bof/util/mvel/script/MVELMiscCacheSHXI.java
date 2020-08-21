package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;

public class MVELMiscCacheSHXI extends AbstractReadOnlyCache {
	private static final transient Logger log = Logger.getLogger(MVELMiscCacheSHXI.class);
	@Override
	public Map<String, Object> loadData() throws Exception {
		MVELCacheDataSHXI mvelData = new MVELCacheDataSHXI();
		
		DBConnection conn = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		String sql = "SELECT * FROM td_s_crm_mvelmisc where EFF_STATE='1' and  sysdate between eff_start_date and eff_end_date";
		//log.info("("===============MVELMiscCacheSHXI==liujian===111=================="+sql);
		try {
			conn = new DBConnection("cen1", false, false);
			//log.info("("===============MVELMiscCacheSHXI==liujian===222=================="+conn.getName());
			statement = conn.prepareStatement(sql);
			result = statement.executeQuery();
			//log.info("("===============MVELMiscCacheSHXI==liujian==chaxunjieshu==333==================");
			while (result.next()) {
				try {
					//log.info("("===============MVELMiscCacheSHXI==liujian==jiexi0000000==================");
					processResultSet(result, mvelData);
					//log.info("("===============MVELMiscCacheSHXI==liujian==jiexi1111111==================");
				} catch (Exception e) {
					//log.info("("===============MVELMiscCacheSHXI==liujian==jiexi=Exception==111===============" + e.getCause());
					log.error(e.getMessage(), e);
				}
			}
			//log.info("("===============MVELMiscCacheSHXI==liujian==chaxunjieshu...==================");
			Map<String, Object> cache = new HashMap<String, Object>();
			cache.put("CRM_MVEL_CACHE", mvelData);
			return cache;
		} catch (Exception e) {
			//log.info("("===============MVELMiscCacheSHXI==liujian==yichang==Exception====2222=============="+e.getCause());
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private void processResultSet(ResultSet rs, MVELCacheDataSHXI mvelData) throws SQLException {
		String miscName = rs.getString("MISC_NAME");
		String miscType = rs.getString("MISC_TYPE");
		//log.info("("===============MVELMiscCacheSHXI==liujian==jiexiXXXXXXXXX=================="+miscType+"===="+miscName);
		String s1 = rs.getString("SCRIPT_1");
		String s2 = rs.getString("SCRIPT_2");
		String s3 = rs.getString("SCRIPT_3");
		String s4 = rs.getString("SCRIPT_4");
		String s5 = rs.getString("SCRIPT_5");
		String s6 = rs.getString("SCRIPT_6");
		String s7 = rs.getString("SCRIPT_7");
		String s8 = rs.getString("SCRIPT_8");
		String s9 = rs.getString("SCRIPT_9");
		String s10 = rs.getString("SCRIPT_10");
		if (s1 == null)
			s1 = "";
		if (s2 == null)
			s2 = "";
		if (s3 == null)
			s3 = "";
		if (s4 == null)
			s4 = "";
		if (s5 == null)
			s5 = "";
		if (s6 == null)
			s6 = "";
		if (s7 == null)
			s7 = "";
		if (s8 == null)
			s8 = "";
		if (s9 == null)
			s9 = "";
		if (s10 == null)
			s10 = "";

		String miscScript = s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 + s10;
		mvelData.addByType(miscType, miscName, miscScript);
	}
}
