
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class OrderOpenDao
{

    /**
     * 记录跨省工单状态
     * 
     * @author Liuzz
     * @throws Exception
     */
    public static boolean inserPotradeState(IData para) throws Exception
    {

        boolean result = false;
        result = Dao.insert("TF_B_POTRADE_STATE", para, Route.getJourDb(Route.CONN_CRM_CG));
        return result;
    }

    /**
     * 记录跨省工单状态信息
     * 
     * @author Liuzz
     * @throws Exception
     */
    public static void inserPotradeStateAttr(IDataset paras) throws Exception
    {

        StringBuilder sql = new StringBuilder(
                " insert into tf_b_potrade_state_attr (SYNC_SEQUENCE, INFO_TAG, INFO_TYPE, SUB_INFO_TYPE, ORDERING_ID, ATTR_NAME, ATTR_CODE, ATTR_VALUE, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_NUM1, RSRV_NUM2, RSRV_DATE1, RSRV_DATE2) ");
        sql.append("values (:SYNC_SEQUENCE, :INFO_TAG, :INFO_TYPE, :SUB_INFO_TYPE, :ORDERING_ID, :ATTR_NAME, :ATTR_CODE, :ATTR_VALUE, :RSRV_STR1, :RSRV_STR2, :RSRV_STR3, :RSRV_NUM1, :RSRV_NUM2, :RSRV_DATE1, :RSRV_DATE2)");
        Dao.executeBatch(sql, paras,Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 记录跨省工单状态产品
     * 
     * @author Liuzz
     * @throws Exception
     */
    public static boolean inserProductTrade(IData para) throws Exception
    {

        boolean result = false;
        result = Dao.insert("TF_B_PRODUCTTRADE", para, Route.getJourDb(Route.CONN_CRM_CG));
        return result;
    }

    /**
     * 记录报文信息
     * 
     * @throws Exception
     */
    public boolean inserPotrade(IData para) throws Exception
    {

        boolean result = false;
        result = Dao.insert("TF_B_POTRADE", para);
        return result;
    }

    /**
     * 记录报文相关信息
     * 
     * @throws Exception
     */
    public boolean inserPotradePlus(IData para) throws Exception
    {

        boolean result = false;
        result = Dao.insert("TF_B_POTRADEPLUS", para);
        return result;
    }

    /**
     * 添加产品扩展属性信息到tf_b_potradeplus表
     * 
     * @author Liuyt3
     */
    public boolean insertPoProductExtends(IData data) throws Exception
    {

        IData temp = Dao.qryByPK("TF_B_POTRADEPLUS", data);
        if (temp == null)
        {
            return Dao.insert("TF_B_POTRADEPLUS", data);
        }
        return Dao.update("TF_B_POTRADEPLUS", data);
    }

    /**
     * 设置工单完工
     */
    public void modifyOrderInfoState(String order_no) throws Exception
    {
        IData data = new DataMap();
        data.put("ORDER_NO", order_no);
        // 完成状态
        SQLParser parser = new SQLParser(data);
        parser.addSQL("update TF_B_POTrade set TRADE_STATE = '2' where PRODUCTORDERNUMBER = :ORDER_NO");
        Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }

}
