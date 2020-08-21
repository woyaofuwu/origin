
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeGrpMerchDiscntInfoQry
{

    /**
     * 根据tradeId查询TF_B_TRADE_GRP_MERCH_DISCNT表信息
     * 
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset qryMerchDiscntInfoByTradeId(String trade_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("select TO_CHAR(a.trade_id) trade_id, ");
        sql.append("a.accept_month, ");
        sql.append("a.user_id, ");
        sql.append("a.merch_spec_code, ");
        sql.append("a.merch_discnt_code, ");
        sql.append("a.start_date, ");
        sql.append("a.end_date, ");
        sql.append("a.modify_tag ");
        sql.append("FROM TF_B_TRADE_GRP_MERCH_DISCNT a ");
        sql.append("WHERE a.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");

        return Dao.qryBySql(sql, inparams, Route.CONN_CRM_CG);
    }
}
