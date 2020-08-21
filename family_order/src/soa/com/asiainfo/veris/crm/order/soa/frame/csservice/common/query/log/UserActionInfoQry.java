
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserActionInfoQry
{
    /**
     * 查询订购交互行为日志查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryUserAction(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from tl_b_useraction ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL(" and serial_number = :SERIAL_NUMBER ");
        parser.addSQL(" and sp_id = :SP_ID");
        parser.addSQL(" and useraction = :USERACTION ");

        return Dao.qryByParse(parser, pagination);
    }
}
