SELECT b.partition_id,
       to_char(b.user_id) user_id,
       b.service_type,
       b.deal_flag,
       to_char(b.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(b.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(b.update_date, 'yyyy-mm-dd hh24:mi:ss') update_date
  from tf_bh_trade a, tf_f_user_svcallowance b
 where a.trade_type_code = '370'
   and a.user_id = b.user_id
   and a.RSRV_STR2 IN ('80', '90')
   and b.service_type =
       decode(a.rsrv_str3, 'FF', '199', '1' || a.rsrv_str3)
   and a.trade_staff_id = :TRADE_STAFF_ID 
   AND b.update_date between
       TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')) AND
       TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))+1