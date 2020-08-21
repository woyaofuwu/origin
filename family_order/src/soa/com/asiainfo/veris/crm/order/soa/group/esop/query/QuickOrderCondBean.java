package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class QuickOrderCondBean
{
    /**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformQuickOrderCond(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_QUICKORDER_COND", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryQuickorderCondInfos(String ibsysid) throws Exception{
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_B_EOP_QUICKORDER_COND T WHERE T.IBSYSID =:IBSYSID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static int delQuickorderCond(String ibsysid) throws Exception{
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM TF_B_EOP_QUICKORDER_COND T WHERE T.IBSYSID =:IBSYSID ");
        
        return Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    /**
     * 根据CUST_ID、产品编码以及号码查询该客户订购的商务宽带中最大的号码
     * 
     * @param data
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset getMaxSerNumberSN(IData map) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", map.getString("CUST_ID"));
        param.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.SERIAL_NUMBER   ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_COND T  ");
        parser.addSQL(" WHERE T.CUST_ID=:CUST_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER  like :SERIAL_NUMBER || '%'  ");
        parser.addSQL(" ORDER  BY SERIAL_NUMBER  DESC ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }
    
    
    /**
     * 根据CUST_ID、IBSYSID以及产品编码获取客户预受理快速办理的产品相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xieqj
     */
    public static IDataset getEspProductApplyInfo(IData map) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", map.getString("CUST_ID"));
        param.put("IBSYSID", map.getString("IBSYSID"));
        param.put("PRODUCT_ID", map.getString("PRODUCT_ID"));

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.SUB_IBSYSID,  T.IBSYSID NODE_ID, T.ACCEPT_MONTH, T.CUST_ID, T.PRODUCT_ID,");
        parser.addSQL(" T.CODING_STR1, T.CODING_STR2, T.CODING_STR3, T.CODING_STR4, T.CODING_STR5, T.CODING_STR6, T.CODING_STR7, T.CODING_STR8, T.CODING_STR9,T.CODING_STR10, T.UPDATE_TIME, T.RSRV_STR1, T.RSRV_STR2");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_COND T  ");
        parser.addSQL(" WHERE T.CUST_ID=:CUST_ID ");
        parser.addSQL(" AND T.IBSYSID=:IBSYSID  ");
        parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID  ");
        parser.addSQL(" ORDER  BY ACCEPT_MONTH, UPDATE_TIME  DESC ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 根据CUST_ID、IBSYSID以及产品编码获取客户预受理快速办理的产品相关信息
     *
     * @param
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public static IDataset getConInfoByIbsysidAndSnAndProductId(IData param) throws Exception {
        IData input = new DataMap();
        input.put("IBSYSID", param.getString("IBSYSID"));
        input.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        input.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        input.put("RSRV_STR1", param.getString("RSRV_STR1"));
        input.put("RSRV_STR5", param.getString("RSRV_STR5"));
        input.put("CUST_ID", param.getString("CUST_ID"));
        SQLParser parser = new SQLParser(input);

        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_B_EOP_QUICKORDER_COND T  ");
        parser.addSQL(" WHERE 1=1  ");
        parser.addSQL(" AND T.IBSYSID=:IBSYSID  ");
        parser.addSQL(" AND T.RSRV_STR1=:RSRV_STR1 ");

        if(StringUtils.isNotEmpty(param.getString("SERIAL_NUMBER")))
        {
            parser.addSQL(" AND T.SERIAL_NUMBER=:SERIAL_NUMBER  ");
        }
        
        if(StringUtils.isNotEmpty(param.getString("PRODUCT_ID")))
        {
            parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID  ");
        }

        if(StringUtils.isNotEmpty(param.getString("RSRV_STR5")))
        {
            parser.addSQL(" AND T.RSRV_STR5=:RSRV_STR5  ");
        }

        if(StringUtils.isNotEmpty(param.getString("CUST_ID")))
        {
            parser.addSQL(" AND T.CUST_ID=:CUST_ID  ");
        }

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

}
