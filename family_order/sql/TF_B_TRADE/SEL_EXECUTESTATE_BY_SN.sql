SELECT to_char(trade_id) trade_id,'' order_id,'' bpm_id,trade_type_code,'' in_mode_code,-1 priority,subscribe_state,'' next_deal_tag,product_id,brand_code,'' user_id,'' cust_id,'' acct_id,serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,-1 accept_month,trade_staff_id,trade_depart_id,'' trade_city_code,'' trade_eparchy_code,'' term_ip,'' eparchy_code,'' city_code,'' olcom_tag,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,0  oper_fee,0  foregift,0 advance_pay,'' invoice_no,'' fee_state,''  fee_time,'' fee_staff_id,cancel_tag,to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,cancel_staff_id,cancel_depart_id,'' cancel_city_code,'' cancel_eparchy_code,'' process_tag_set,decode(subscribe_state,'0','未启动', '1','派发启动', '2','启动', '3','失败', '4','派发启动成功','5','再处理','6','再处理失败','7','人工挂起','8','挂起激活','9','正常完工','10','人工取消','11','挂起等待','未知') rsrv_str1,decode(rsrv_str2,'0','未启动','1','非拆分工单正在执行','3','处理成功','4','处理失败','8','拆分工单正在执行','未知状态') rsrv_str2,'' rsrv_str3,'' rsrv_str4,'' rsrv_str5,'' rsrv_str6,'' rsrv_str7,rsrv_str8,rsrv_str9,'' rsrv_str10,'' remark
from
(SELECT a.trade_id,a.trade_type_code,a.subscribe_state,a.product_id,a.brand_code,a.serial_number,a.cust_name,accept_date,a.trade_staff_id,a.trade_depart_id,a.exec_time,a.finish_date, a.cancel_tag,a.cancel_date cancel_date,a.cancel_staff_id,a.cancel_depart_id,
 b.err_detail_desc rsrv_str8 ,c.remark3 rsrv_str9,c.olcom_state rsrv_str2
  FROM tf_b_trade a,tl_bpm_error_log b ,ti_c_olcomwork c
 WHERE a.trade_id=b.trade_id(+)
   AND a.trade_id=c.trade_id(+)
    AND a.serial_number=:SERIAL_NUMBER
    AND a.ACCEPT_DATE+0 BETWEEN to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') AND to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
    AND ((a.trade_type_code=to_char(:TRADE_TYPE_CODE)) OR (:TRADE_TYPE_CODE='-1'))
    AND (a.subscribe_state=:SUBSCRIBE_STATE OR :SUBSCRIBE_STATE='-1')
    AND (c.olcom_state=:OLCOM_STATE OR :OLCOM_STATE='-1')
UNION ALL
SELECT a.trade_id,a.trade_type_code,a.subscribe_state,a.product_id,a.brand_code,a.serial_number,a.cust_name,accept_date,a.trade_staff_id,a.trade_depart_id,a.exec_time,a.finish_date, a.cancel_tag,a.cancel_date cancel_date,a.cancel_staff_id,a.cancel_depart_id,
 b.err_detail_desc rsrv_str8 ,c.remark3 rsrv_str9,c.olcom_state rsrv_str2
  FROM tf_bh_trade a,tl_bpm_error_log b ,ti_ch_olcomwork c
 WHERE a.trade_id=b.trade_id(+)
   AND a.trade_id=c.trade_id(+)
    AND a.serial_number=:SERIAL_NUMBER
    AND a.ACCEPT_DATE+0 BETWEEN to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') AND to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
    AND ((a.trade_type_code=to_char(:TRADE_TYPE_CODE)) OR (:TRADE_TYPE_CODE='-1'))
    AND (a.subscribe_state=:SUBSCRIBE_STATE OR :SUBSCRIBE_STATE='-1')
    AND (c.olcom_state=:OLCOM_STATE OR :OLCOM_STATE='-1')
)