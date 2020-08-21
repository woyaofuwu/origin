
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserCommUtil
{

    /**
     *"判断输入号码是否已被占用(TF_B_TRADE表有记录)
     */
    public static IDataset existsSerialNumberTrade(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TD_S_CPARAM", "ExistsSerialNumberTrade", param);
    }

    /**
     *判断输入号码是否已存在(TF_F_USER表有记录)
     */
    public static IDataset existsSerialNumberUser(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TD_S_CPARAM", "ExistsSerialNumberUser", param);
    }

    public static IDataset qryByParse(SQLParser parser) throws Exception
    {
        return Dao.qryByParse(parser);
    }

    public static IDataset qryByParse(SQLParser parser, Pagination page) throws Exception
    {
        return Dao.qryByParse(parser, page);
    }

    public static IDataset qryByParse(SQLParser parser, Pagination page, String routeId) throws Exception
    {
        return Dao.qryByParse(parser, page, routeId);
    }

    public static IDataset qryByParse(SQLParser parser, String routeId) throws Exception
    {
        return Dao.qryByParse(parser, routeId);
    }
}
