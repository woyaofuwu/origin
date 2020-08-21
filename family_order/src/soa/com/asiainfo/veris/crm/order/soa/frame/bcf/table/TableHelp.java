
package com.asiainfo.veris.crm.order.soa.frame.bcf.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.biz.service.BizRoute;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.util.DaoHelper;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.ConnMgr;

public final class TableHelp
{
    /**
     * 根据表名获取表列
     * 
     * @param tableName
     * @return
     * @throws Exception
     */
    public static IColumnObject[] getTableColunms(String tableName) throws Exception
    {
        IColumnObject[] tableColumns = getTableColunms(tableName, BizRoute.getRouteId());
        return tableColumns;
    }

    /**
     * 根据表名获取表列
     * 
     * @param tableName
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IColumnObject[] getTableColunms(String tableName, String eparchyCode) throws Exception
    {
        DBConnection conn = ConnMgr.getConnection(eparchyCode);
        IColumnObject[] tableColumns = DaoHelper.getColumns(conn, tableName);
        return tableColumns;
    }

    public static List<String> preprocStatement(String sqlstr) throws Exception
    {
        Pattern patParam = Pattern.compile("(:[a-zA-Z_0-9\\$]*)");
        Pattern patQuote = Pattern.compile("('[^']*')");

        List<String> quoteRanges = new ArrayList();
        Matcher matcher = patQuote.matcher(sqlstr);
        while (matcher.find())
        {

            int start = matcher.start();
            String text = matcher.group();
            int length = text.length();
            quoteRanges.add(text + "_" + start + "_" + length);
        }
        matcher = patParam.matcher(sqlstr);
        List<String> keys = new ArrayList();
        while (matcher.find())
        {
            String key = matcher.group().substring(1);
            if (!quoteRanges.isEmpty())
            {
                boolean skip = false;
                int pos = matcher.start();
                Iterator<String> it = quoteRanges.iterator();
                while (it.hasNext())
                {
                    String[] r = it.next().split("_");
                    if ((pos >= Integer.parseInt(r[1])) && (pos < Integer.parseInt(r[1]) + Integer.parseInt(r[2])))
                    {
                        skip = true;
                        break;
                    }
                }
                if (skip)
                {
                    continue;
                }
            }
            keys.add(key);
        }

        return keys;
    }
}
