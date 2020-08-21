
package com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.BizConstants;
import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;

public final class DataFuzzyCache extends AbstractReadOnlyCache
{

    public class FuzzyRule implements Serializable
    {
        private IData fuzzyRuleObj;

        private String inModeCode;

        private String rightCode;

        private String svcName;

        public FuzzyRule(String svcName, String rightCode, String inModeCode, IData fuzzyRuleObj)
        {
            this.svcName = svcName;
            this.rightCode = rightCode;
            this.inModeCode = inModeCode;
            this.fuzzyRuleObj = fuzzyRuleObj;
        }

        public IData getFuzzyRule()
        {
            return fuzzyRuleObj;
        }

        public String getInModeCode()
        {
            return inModeCode;
        }

        public String getRightCode()
        {
            return rightCode;
        }

        public String getSvcName()
        {
            return svcName;
        }
    }

    // private final static Logger logger = Logger.getLogger(DataFuzzyCache.class);

    private IData getFuzzyRuleMap(String fuzzyRule)
    {
        IData fuzzyRuleObj = new DataMap();

        String tmp = "";
        int index = 0;
        String kKey = "";
        String kValue = "";
        String fColumn = "";
        String fType = "";

        // 模糊规则拆分为组
        String[] szGroup = StringUtils.split(fuzzyRule, ";");

        for (int kIndex = 0, kLength = szGroup.length; kIndex < kLength; kIndex++)
        {
            tmp = szGroup[kIndex].trim();

            index = tmp.indexOf("=");

            if (index == -1)
            {
                return fuzzyRuleObj;
            }

            kKey = tmp.substring(0, index).trim();
            kValue = tmp.substring(index + 1).trim();

            // if (logger.isDebugEnabled())
            // {
            // logger.debug("kKey=[" + kKey + "] kValue=[" + kValue + "]");
            // }

            IData columnType = new DataMap();

            fuzzyRuleObj.put(kKey, columnType);

            // 需要模糊的字段
            String[] szColumn = StringUtils.split(kValue, ",");

            // 遍历模糊字段
            for (int jIndex = 0, jLength = szColumn.length; jIndex < jLength; jIndex++)
            {
                tmp = szColumn[jIndex].trim();

                index = tmp.indexOf(":");

                if (index == -1)
                {
                    fColumn = tmp;
                    fType = "";
                }
                else
                {
                    fColumn = tmp.substring(0, index).trim();
                    fType = tmp.substring(index + 1).trim();
                }

                // if (logger.isDebugEnabled())
                // {
                // logger.debug("fColumn=[" + fColumn + "] fType=[" + fType + "]");
                // }

                columnType.put(fColumn, fType);
            }
        }

        return fuzzyRuleObj;
    }

    @Override
    public Map<String, Object> loadData() throws Exception
    {
        Map<String, Object> rtn = new HashMap<String, Object>();

        DBConnection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT SVC_NAME, FUZZY_RULE, RIGHT_CODE, IN_MODE_CODE FROM TD_B_DATA_FUZZY T WHERE SYSDATE BETWEEN T.START_TIME AND T.END_TIME";

        try
        {
            conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                String svcName = rs.getString("SVC_NAME");
                String fuzzyRule = rs.getString("FUZZY_RULE");
                String rightCode = rs.getString("RIGHT_CODE");
                String inModeCode = rs.getString("IN_MODE_CODE");

                // if (logger.isDebugEnabled())
                // {
                // logger.debug("svcName=[" + svcName + "]");
                // }

                IData fuzzyRuleMap = getFuzzyRuleMap(fuzzyRule);

                // 构造对象
                FuzzyRule fuzzyRuleObj = new FuzzyRule(svcName, rightCode, inModeCode, fuzzyRuleMap);

                rtn.put(svcName, fuzzyRuleObj);
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
