
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class InterfaceInfoQry
{
    public static IDataset getInterfaceParam(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("   SELECT * ");
        parser.addSQL("     FROM TD_S_INTERFACE_PARAMS ");
        parser.addSQL("    WHERE INTER_ID = TO_NUMBER(:INTER_ID) ");
        parser.addSQL("      AND INTER_KEY = :INTER_KEY ");
        parser.addSQL(" ORDER BY PARAM_ID ASC ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getSceneById(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("   SELECT * ");
        parser.addSQL("     FROM TD_S_INTERFACE_SCENE ");
        parser.addSQL("    WHERE INTER_ID = TO_NUMBER(:INTER_ID) ");
        parser.addSQL(" ORDER BY SCENE_ID ASC ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getSceneInfoById(IData data2) throws Exception
    {
        SQLParser parser = new SQLParser(data2);
        parser.addSQL("   SELECT * ");
        parser.addSQL("     FROM TD_S_INTERFACE_SCENE ");
        parser.addSQL("    WHERE SCENE_ID = TO_NUMBER(:SCENE_ID) AND INTER_ID = TO_NUMBER(:INTER_ID) ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    private static IDataset getSpiltSet(String source, int byteLength)
    {
        byte[] sByte = source.getBytes();
        char[] sChar = source.toCharArray();
        IDataset dataset = new DatasetList();
        if (sByte.length <= byteLength)
        {
            dataset.add(source);
        }
        else
        {
            int byleCount = 0;
            int first = 0;
            for (int i = 0; i < sChar.length; i++)
            {
                if ((int) sChar[i] > 0x80)
                {
                    byleCount += 2;
                }
                else
                {
                    byleCount += 1;
                }
                if (byleCount == byteLength)
                {
                    if (first == 0)
                        dataset.add(new String(sChar, first, i + 1));
                    else
                        dataset.add(new String(sChar, first, i - first - 1));
                    first = i;
                    byleCount = 0;
                }
                if (byleCount == byteLength + 1)
                {
                    if (first == 0)
                        dataset.add(new String(sChar, first, i));
                    else
                        dataset.add(new String(sChar, first, i - first - 1));
                    first = i - 1;
                    byleCount = 2;
                }
            }
            if (byleCount != 0)
                dataset.add(new String(sChar, first + 1, sChar.length - first - 1));
        }
        return dataset;
    }

    private static boolean insertLog(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" INSERT INTO TL_INTERFACE_LOG ");
        parser.addSQL(" (INVOKE_DATE, INTER_ID, SCENE_VL, INTER_CODE, UPDATE_STAFF_ID) ");
        parser.addSQL(" VALUES ");
        parser.addSQL(" (TO_DATE(:INVOKE_DATE, 'yyyy-mm-dd hh24:mi:ss'), :INTER_ID, :SCENE_VL, :INTER_CODE, :UPDATE_STAFF_ID) ");
        int num = Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
        if (num > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean logInterface(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("   SELECT * ");
        parser.addSQL("     FROM TL_INTERFACE_LOG ");
        parser.addSQL("    WHERE INTER_ID = TO_NUMBER(:INTER_ID) ");
        parser.addSQL("    AND SCENE_VL = :SCENE_VL ");
        parser.addSQL("    AND INTER_CODE = :INTER_CODE ");

        IDataset ids = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(ids))
        {
            return insertLog(data);
        }
        else
        {
            return false;
        }
    }

    public static IDataset queryAllInterfaceInfos(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT INTER_ID, CODE, INTER_ID || '★★' || INTER_NAME AS INTER_NAME, LOGININ_INFOS, ADDR, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME, VALID_FLAG, SUBSYS_CODE ");
        parser.addSQL("   FROM TD_S_INTERFACE_INFOS ");
        parser.addSQL("  WHERE SUBSYS_CODE = :SUBSYS_CODE ORDER BY INTER_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryInterfaceById(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TD_S_INTERFACE_INFOS ");
        parser.addSQL(" WHERE INTER_ID = TO_NUMBER(:INTER_ID) ORDER BY INTER_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static boolean upInterfaceResultById(IData data) throws Exception
    {
        String sResults = data.getString("RESULTS", "");
        IDataset aResults = getSpiltSet(sResults, 2000);
        IData param = new DataMap();
        param.put("SCENE_ID", data.getString("SCENE_ID"));
        param.put("INTER_ID", data.getString("INTER_ID"));
        int size = aResults.size();
        for (int i = 0; i < size; i++)
        {
            param.put("RESULTS" + (i + 1), (String) aResults.get(i));
        }

        for (int i = size; i < 10; i++)
        {
            param.put("RESULTS" + (i + 1), null);
        }
        return Dao.save("TD_S_INTERFACE_SCENE", param, new String[]
        { "SCENE_ID", "INTER_ID" }, Route.CONN_CRM_CEN);
    }
}
