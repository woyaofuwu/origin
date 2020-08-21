
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class InterfaceCreateQry
{

    public static boolean createInfosByParam(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" INSERT INTO TD_S_INTERFACE_INFOS ");
        parser.addSQL(" (INTER_ID, CODE, INTER_NAME, LOGININ_INFOS, ADDR , UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME, VALID_FLAG, SUBSYS_CODE) ");
        parser.addSQL(" VALUES ");
        parser.addSQL(" (SEQ_INFOS_ID.NEXTVAL, :CODE, :INTER_NAME, :LOGININ_INFOS, :ADDR, :UPDATE_STAFF_ID, :UPDATE_DEPART_ID, TO_DATE(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'), :VALID_FLAG, :SUBSYS_CODE) ");
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

    public static boolean createSceneByParam(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" INSERT INTO TD_S_INTERFACE_SCENE ");
        parser.addSQL(" (SCENE_ID, INTER_ID, SCENE_NM, SCENE_VL, VK_IP, VK_DATE , RESULTS1, RESULTS2, RESULTS3, RESULTS4, RESULTS5, RESULTS6, RESULTS7, RESULTS8, RESULTS9, RESULTS10) ");
        parser.addSQL(" VALUES ");
        parser.addSQL(" (SEQ_SCENE_ID.NEXTVAL, :INTER_ID, :SCENE_NM, :SCENE_VL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL) ");
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

    public static boolean createTabByParam(String tabName, IData param) throws Exception
    {
        return Dao.insert(tabName, param, Route.CONN_CRM_CEN);
    }

    public static boolean deleteInterfaceScene(IData param) throws Exception
    {
        int num[] = Dao.delete("TD_S_INTERFACE_SCENE", new DatasetList(param), new String[]
        { "SCENE_ID" }, Route.CONN_CRM_CEN);
        if (num.length > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static IDataset queryInterfaceByCode(String code) throws Exception
    {
        IData param = new DataMap();
        param.put("CODE", code);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT INTER_ID, CODE, INTER_ID || '★★' || INTER_NAME AS INTER_NAME, LOGININ_INFOS, ADDR, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME, VALID_FLAG, SUBSYS_CODE ");
        parser.addSQL("   FROM TD_S_INTERFACE_INFOS ");
        parser.addSQL("  WHERE CODE = :CODE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset querySceneByInterId(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("   SELECT * ");
        parser.addSQL("     FROM TD_S_INTERFACE_SCENE ");
        parser.addSQL("    WHERE INTER_ID = TO_NUMBER(:INTER_ID) ");
        parser.addSQL(" ORDER BY SCENE_ID ASC ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static boolean updateInfosByInterId(IData param) throws Exception
    {
        return Dao.save("TD_S_INTERFACE_INFOS", param, new String[]
        { "INTER_ID" }, Route.CONN_CRM_CEN);
    }
}
