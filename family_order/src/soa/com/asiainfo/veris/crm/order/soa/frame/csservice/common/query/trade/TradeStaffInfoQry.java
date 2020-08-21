
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: TradeStaff.java
 * @Description: TF_BH_TRADE_STAFF相关sql语句
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午05:12:50 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-15 liuke v1.0.0 修改原因
 */
public class TradeStaffInfoQry
{

    public static void insertStaffTradeInfo(String tradeId) throws Exception
    {
        IData param = new DataMap();
        String day = SysDateMgr.getCurDay();
        param.put("TRADE_ID", tradeId);
        param.put("DAY", day);

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO tf_bh_trade_staff");
        sql.append(" (trade_id, accept_month, batch_id, order_id, prod_order_id, bpm_id, campn_id, trade_type_code, priority, subscribe_type, subscribe_state, next_deal_tag, in_mode_code, cust_id, cust_name, user_id, acct_id, serial_number, net_type_code, eparchy_code, city_code, product_id, brand_code, cust_id_b, user_id_b, acct_id_b, serial_number_b, cust_contact_id, serv_req_id, intf_id, accept_date, trade_staff_id, trade_depart_id, trade_city_code, trade_eparchy_code, term_ip, oper_fee, foregift, advance_pay, invoice_no, fee_state, fee_time, fee_staff_id, process_tag_set, olcom_tag, finish_date, exec_time, exec_action, exec_result, exec_desc, cancel_tag, cancel_date, cancel_staff_id, cancel_depart_id, cancel_city_code, cancel_eparchy_code, update_time, update_staff_id, update_depart_id, remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10,day) ");
        sql.append("SELECT trade_id, accept_month, batch_id, order_id, prod_order_id, bpm_id, campn_id, trade_type_code, priority, subscribe_type, subscribe_state, next_deal_tag, in_mode_code, cust_id, cust_name, user_id, acct_id, serial_number, net_type_code, eparchy_code, city_code, product_id, brand_code, cust_id_b, user_id_b, acct_id_b, serial_number_b, cust_contact_id, serv_req_id, intf_id, accept_date, trade_staff_id, trade_depart_id, trade_city_code, trade_eparchy_code, term_ip, oper_fee, foregift, advance_pay, invoice_no, fee_state, fee_time, fee_staff_id, process_tag_set, olcom_tag, finish_date, exec_time, exec_action, exec_result, exec_desc, cancel_tag, cancel_date, cancel_staff_id, cancel_depart_id, cancel_city_code, cancel_eparchy_code, update_time, update_staff_id, update_depart_id, remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10 ");
        sql.append(",:DAY ");
        sql.append("FROM tf_bh_trade ");
        sql.append("WHERE trade_id= :TRADE_ID ");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
    }

    public static IDataset queryTradeStaffByTradeId(String tradeId, String cancelTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_BH_TRADE_STAFF", "SEL_BY_TRADEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
