
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeRelaBBInfoQry extends CSBizBean
{
    /**
     * @description 根据userIdB查询没有完工的BB关系
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset getNotFinishTradeBB(String USER_ID_B, String relationTypeCode, String routeId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("USER_ID_B", USER_ID_B);
        inData.put("RELATION_TYPE_CODE", relationTypeCode);
        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.MODIFY_TAG");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION_BB b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
        sp.addSQL(" and b.relation_type_code=:RELATION_TYPE_CODE");
        sp.addSQL(" order by a.exec_time");
        return Dao.qryByParse(sp, Route.getJourDb(routeId));
    }

    /**
     * @description 根据台帐编号获取BB台帐关系表信息
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset qryRelaBBInfoListByTradeIdForGrp(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT to_char(TRADE_ID) TRADE_ID,ACCEPT_MONTH ,to_char(USER_ID_A) USER_ID_A,SERIAL_NUMBER_A , ");
        sql.append("to_char(USER_ID_B) USER_ID_B,SERIAL_NUMBER_B ,RELATION_TYPE_CODE ,ROLE_TYPE_CODE ,ROLE_CODE_A , ");
        sql.append("ROLE_CODE_B ,ORDERNO ,SHORT_CODE ,to_char(INST_ID) INST_ID, ");
        sql.append("to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,MODIFY_TAG , ");
        sql.append("to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID ,UPDATE_DEPART_ID ,REMARK ,RSRV_NUM1 ,RSRV_NUM2 , ");
        sql.append("RSRV_NUM3 ,RSRV_NUM4 ,RSRV_NUM5 ,RSRV_STR1 ,RSRV_STR2 ,RSRV_STR3 , ");
        sql.append("to_char(RSRV_STR4) RSRV_STR4,to_char(RSRV_STR5) RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1 ,RSRV_TAG2 ,RSRV_TAG3 ");
        sql.append("FROM tf_b_trade_relation_bb ");
        sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");

        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @description 根据集团用户编号和成员用户编号查询台帐表，判断当前成员是否为重复添加
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset qryRelaBBInfoListByuserIdAB(String USER_ID_A, String USER_ID_B, String routId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("USER_ID_A", USER_ID_A);
        inData.put("USER_ID_B", USER_ID_B);

        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.MODIFY_TAG");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        sp.addSQL(" and a.trade_type_code in ('4694','4695','4697')");
        sp.addSQL(" and b.user_id_a=:USER_ID_A");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
        sp.addSQL(" and a.update_staff_id='IBOSS000'");
        sp.addSQL(" order by a.exec_time");

        return Dao.qryByParse(sp, Route.getJourDb(routId));
    }

    public static IDataset qryJKDTRelaBBInfoListByuserIdAB(String USER_ID_A, String USER_ID_B, String routId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("USER_ID_A", USER_ID_A);
        inData.put("USER_ID_B", USER_ID_B);

        SQLParser sp = new SQLParser(inData);
        sp.addSQL("select b.MODIFY_TAG");
        sp.addSQL(" from TF_B_TRADE a, TF_B_TRADE_RELATION b");
        sp.addSQL(" where 1=1");
        sp.addSQL(" and a.trade_id=b.trade_id");
        sp.addSQL(" and a.trade_type_code in ('2351','2352','2353','2354','2355')");
        sp.addSQL(" and b.user_id_a=:USER_ID_A");
        sp.addSQL(" and b.user_id_b=:USER_ID_B");
        sp.addSQL(" and a.update_staff_id='IBOSS000'");
        sp.addSQL(" order by a.exec_time");

        return Dao.qryByParse(sp, Route.getJourDb(routId));
    }
}
