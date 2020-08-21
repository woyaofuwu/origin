
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
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 规则定义缓存
 * 
 * @className: BreDefinitionCache
 * @description: 缓存规则脚本定义
 */
public final class BreDefinitionCache extends AbstractReadOnlyCache
{

	public static class BreDefinition implements Serializable
	{

		private static final long serialVersionUID = -4423322164752544662L;

		private final Object objScript;

		private final String scriptId;

		private final String scriptPath;

		public BreDefinition(String strScriptId, String strScriptPath, Object script)
		{
			this.scriptId = strScriptId;
			this.scriptPath = strScriptPath;
			this.objScript = script;
		}

		public Object getObjScript()
		{
			return objScript;
		}

		public String getScriptId()
		{
			return scriptId;
		}

		public String getScriptPath()
		{
			return scriptPath;
		}
	}

	private static final Logger log = Logger.getLogger(BreDefinitionCache.class);

	/**
	 * 以SCRIPT_ID作为key，缓存SCRIPT_PATH
	 */
	@Override
	public Map<String, Object> loadData() throws Exception
	{
		Map<String, Object> rtn = new HashMap<String, Object>();

		// DataInput input = new DataInput();
		// IDataset datas = ServiceFactory.call("CS.Bre.LoadDefinitionCache", input).getData();

		DBConnection conn = null;
		Statement stmt = null;
		ResultSet data = null;

		String sql = "SELECT SCRIPT_ID, SCRIPT_PATH FROM TD_BRE_DEFINITION";

		try
		{
			conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

			stmt = conn.createStatement();
			data = stmt.executeQuery(sql);

			while (data.next())
			{
				String strScriptId = data.getString("SCRIPT_ID");
				String strScriptPath = data.getString("SCRIPT_PATH");

				if (StringUtils.isNotBlank(strScriptPath))
				{
					try
					{
						rtn.put(strScriptId, new BreDefinition(strScriptId, strScriptPath, Class.forName(strScriptPath).getConstructor().newInstance()));
						if (log.isDebugEnabled())
						{
							log.debug(" +++++++ rule script [" + strScriptId + "] [" + strScriptPath + "] load succeed!");
						}
					}
					catch (Exception e)
					{
						log.error("加载BreDefinitionCache发生错误！strScriptId=" + strScriptId, e);
					}
				}
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
