package com.asiainfo.veris.crm.order.soa.group.bat.xhkregcheck;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class XhkRegCheckBean extends CSBizBean
{
    /**
     * 通过operate_id更新未启动批量详情表,更新deal_state/deal_result
     * 
     * @param data
     * @throws Exception
     */
    public static void updateBatDealByOperateIdSn(IData data) throws Exception
    {

        StringBuilder sql = new StringBuilder();

        sql.append(" UPDATE tf_b_trade_batdeal a");
        sql.append(" SET a.deal_time = SYSDATE");
        sql.append(" ,a.DEAL_RESULT = :DEAL_RESULT");
        sql.append(" ,a.DEAL_DESC = :DEAL_DESC");
        sql.append(" where 1=1");
        sql.append(" and a.operate_id = TO_NUMBER(:BATCH_ID)");//GroupBatBaseTrans.java batInitialBase方法作了转换将operate_id存入batch_id
        sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");

        Dao.executeUpdate(sql, data, Route.CONN_CRM_CEN);
    }
    /**
     * 通过batch_id更新未启动批量详情表,更新deal_state/deal_result
     * 
     * @param data
     * @throws Exception
     */
    public static void updateBatDealByBatchIdSn(IData data) throws Exception
    {
        
        StringBuilder sql = new StringBuilder();
        
        sql.append(" UPDATE tf_b_trade_batdeal a");
        sql.append(" SET a.deal_time = SYSDATE");
        sql.append(" ,a.DEAL_RESULT = :DEAL_RESULT");
        sql.append(" ,a.DEAL_DESC = :DEAL_DESC");
        sql.append(" where 1=1");
        sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
        sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        sql.append(" and a.serial_number = :SERIAL_NUMBER");
        
        Dao.executeUpdate(sql, data, Route.CONN_CRM_CEN);
    }
}
