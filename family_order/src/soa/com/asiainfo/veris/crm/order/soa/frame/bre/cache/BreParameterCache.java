
package com.asiainfo.veris.crm.order.soa.frame.bre.cache;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.DBConnection;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;

/**
 * @className: BreParameterCache
 * @description: 缓存规则参数配置
 */
public final class BreParameterCache extends AbstractReadOnlyCache
{

	public static class BreParameter implements Serializable
	{

		private static final long serialVersionUID = 982047738980841476L;

		private final String ruleId;

		private final BreRuleParam ruleParam;

		public BreParameter(String strRuleId, BreRuleParam map)
		{
			this.ruleId = strRuleId;
			this.ruleParam = map;
		}

		public BreRuleParam getRuleData()
		{
			return ruleParam;
		}

		public String getRuleId()
		{
			return ruleId;
		}
	}

	private static final Logger log = Logger.getLogger(BreParameterCache.class);

	/**
	 * 以Rule_id作key, 缓存Rule_Parameter数据
	 */
	@Override
	public Map<String, Object> loadData() throws Exception
	{
		Map<String, Object> rtn = new HashMap<String, Object>();

		// IDataset datas = CSAppCall.call("CS.Bre.LoadParameterCache", new DataMap());

		StringBuilder strbValue = new StringBuilder("");
		String strPrivId = "";
		String strRuleId = "";
		BreRuleParam map = new BreRuleParam();
		DBConnection conn = null;
		Statement stmt = null;
		ResultSet data = null;

		String sql = "SELECT RULE_ID, OBJECT_ID, OPERATION_TYPE, PARAM_VALUE, PARAM_VALUE_EXT1, PARAM_VALUE_EXT2, PARAM_VALUE_EXT3 FROM TD_BRE_PARAMETER T ORDER BY RULE_ID";

		try
		{
			conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

			stmt = conn.createStatement();
			data = stmt.executeQuery(sql);
			while (data.next())
			{
				strRuleId = data.getString("RULE_ID");

				String strObjectId = data.getString("OBJECT_ID");
				String strOpetaionType = data.getString("OPERATION_TYPE");
				String strParamValue = data.getString("PARAM_VALUE");
				String strParamValueExt1 = data.getString("PARAM_VALUE_EXT1");
				String strParamValueExt2 = data.getString("PARAM_VALUE_EXT2");
				String strParamValueExt3 = data.getString("PARAM_VALUE_EXT3");

				if (!strRuleId.equals(strPrivId) && !strPrivId.equals(""))
				{
					rtn.put(strPrivId, new BreParameter(strRuleId, map));
					map = new BreRuleParam();
				}

				if (strParamValue == null || "null".equalsIgnoreCase(strParamValue))
				{
					strParamValue = "";
				}

				strbValue = new StringBuilder("");
				strbValue.append(strParamValue);

				if (strParamValueExt1 != null && !"".equals(strParamValueExt1))
				{
					strbValue.append(strParamValueExt1);
				}
				if (strParamValueExt2 != null && !"".equals(strParamValueExt2))
				{
					strbValue.append(strParamValueExt2);
				}
				if (strParamValueExt3 != null && !"".equals(strParamValueExt3))
				{
					strbValue.append(strParamValueExt3);
				}

				if (!"=".equals(strOpetaionType))
				{
					map.put("BRE_PARAM_VALUE1", strObjectId);
					map.put("BRE_PARAM_VALUE2", strbValue.toString());
					map.put("BRE_PARAM_VALUE3", strOpetaionType);
				}
				else
				{
					map.put(strObjectId, strbValue.toString());
				}

				strPrivId = strRuleId;

				rtn.put(strRuleId, new BreParameter(strRuleId, map));
			}
			data.close();
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
