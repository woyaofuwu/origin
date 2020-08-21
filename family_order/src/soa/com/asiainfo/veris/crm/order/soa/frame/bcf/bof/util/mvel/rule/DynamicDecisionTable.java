
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ailk.cache.localcache.interfaces.IReadWriteCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELCompiledCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.ConnMgr;

public class DynamicDecisionTable
{

    private String tableName = "";

    private String scriptColumn = "";

    private String returnColumn = "";

    private static MVELMiscCache macroCache = null;

    private static IReadWriteCache exprCache = MVELCompiledCache.getStatementCache();

    public DynamicDecisionTable(String scriptColumn) throws Exception
    {
        this.scriptColumn = scriptColumn;
        this.macroCache = CRMMVELMiscCache.getMacroCache();

    }

    public DynamicDecisionTable(String tableName, String scriptColumn, String returnColumn) throws Exception
    {
        this.tableName = tableName;
        this.scriptColumn = scriptColumn;
        this.returnColumn = returnColumn;
        this.macroCache = CRMMVELMiscCache.getMacroCache();

    }

    public DynamicDecisionTable(String tableName, String scriptColumn, String returnColumn, MVELMiscCache cache)
    {
        this.tableName = tableName;
        this.scriptColumn = scriptColumn;
        this.returnColumn = returnColumn;

        this.macroCache = cache;
    }

    public IDataset decide(IDataset list, Object... obsforscript) throws Exception
    {
        IDataset result = new DatasetList();

        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(this.macroCache);
        exector.prepare(obsforscript);

        if (IDataUtil.isNotEmpty(list))
        {
            int size = list.size();
            for (int i = 0; i < size; i++)
            {
                IData data = list.getData(i);
                String script = data.getString(this.scriptColumn);
                if (StringUtils.isBlank(script))
                {
                    result.add(data);
                }
                else
                {
                    Object v = exector.execScript(script);
                    if (v != null && v.equals(new Boolean(true)))
                    {
                        result.add(data);
                    }
                }
            }
        }
        return result;
    }

    public String[] decide(String routeEparchyCode, Object... obsforscript) throws Exception
    {
        String sql = "SELECT NVL(" + scriptColumn + ",'return true;') SCRIPT," + returnColumn + " RID From " + tableName + " ";

        DecisionTableList dtl = new DecisionTableList();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try
        {
            conn = ConnMgr.getDBConn(routeEparchyCode);
            statement = conn.prepareStatement(sql);
            result = statement.executeQuery();
            while (result.next())
            {
                dtl.add(result.getString("SCRIPT"), result.getString("RID"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (result != null)
            {
                try
                {
                    result.close();
                }
                catch (SQLException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        MVELExecutor exector = new MVELExecutor(null, this.exprCache, null);
        exector.setMiscCache(this.macroCache);
        exector.prepare(obsforscript);

        List<String> res = new ArrayList<String>();
        for (DecisionTableItem item : dtl)
        {
            Object v = exector.execScript(item.getScript());
            if (v != null && exector.getBooleanValue(v) == true)
            {
                res.add(item.getRelatedId());
            }
        }
        return res.toArray(new String[]
        {});
    }

}
