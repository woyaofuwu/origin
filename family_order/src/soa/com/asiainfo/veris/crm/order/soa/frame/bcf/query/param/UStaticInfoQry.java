
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UStaticInfoQry
{

    /**
     * TD_S_STATIC表参数翻译
     * 
     * @param type_id
     * @param data_id
     * @return
     * @throws Exception
     */
    public static final String getStaticValue(String type_id, String data_id) throws Exception
    {
        return StaticUtil.getStaticValue(type_id, data_id);
    }
    
    /**
     * 作用：查询静态数据表
     * @param iData
     * @return
     * @throws Exception
     */
    public static IDataset queryStatic(IData iData) throws Exception
    {
        SQLParser parser = new SQLParser(iData);
        parser.addSQL("SELECT TYPE_ID,DATA_ID,DATA_NAME,PDATA_ID FROM TD_S_STATIC ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND TYPE_ID = :TYPE_ID ");
        parser.addSQL("AND DATA_ID = :DATA_ID");
        parser.addSQL("AND PDATA_ID = :PDATA_ID");
        
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
