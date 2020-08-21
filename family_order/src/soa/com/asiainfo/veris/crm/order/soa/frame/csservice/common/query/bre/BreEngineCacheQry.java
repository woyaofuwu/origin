
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bre;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BreEngineCacheQry
{

    /**
     * 获取规则定义
     * 
     * @return
     * @throws Exception
     */
    public static IDataset loadBreDefinition(IData databus) throws Exception
    {

        SQLParser parser = new SQLParser(databus);

        parser.addSQL("SELECT SCRIPT_ID, SCRIPT_PATH FROM TD_BRE_DEFINITION");

        IDataset results = Dao.qryByParse(parser, Route.CONN_CRM_CEN);

        return results;
    }

    /**
     * 获取规则参数定义
     * 
     * @return
     * @throws Exception
     */
    public static IDataset loadBreParameter(IData databus) throws Exception
    {

        SQLParser parser = new SQLParser(databus);

        parser.addSQL("SELECT RULE_ID, OBJECT_ID, OPERATION_TYPE, PARAM_VALUE, PARAM_VALUE_EXT1, PARAM_VALUE_EXT2, PARAM_VALUE_EXT3 FROM TD_BRE_PARAMETER T ORDER BY RULE_ID");

        IDataset results = Dao.qryByParse(parser, Route.CONN_CRM_CEN);

        return results;
    }

}
