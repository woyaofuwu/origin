SELECT b.partition_id,
       a.serial_number user_id,
       b.service_type,
       b.deal_flag,
       to_char(b.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(b.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(b.update_date, 'yyyy-mm-dd hh24:mi:ss') update_date   
  from tf_bh_trade a, tf_f_user_svcallowance b
 where a.trade_type_code = '370'
   and a.user_id = b.user_id
   and a.RSRV_STR2 IN ('80', '90')
   and b.service_type =decode(a.rsrv_str3, 'FF', '199', '1' || a.rsrv_str3)
   and trade_staff_id = :STAFF_ID 
   and a.accept_date<=b.start_date
   and a.finish_date>=b.start_date
   and (b.service_type =:SERVICE_TYPE or :SERVICE_TYPE is null)
   and (b.deal_flag =:DEAL_FLAG or :DEAL_FLAG is null)
   and (b.start_date>=TO_DATE(:START_DATE,'yyyymmddhh24miss') or :START_DATE is null)
   and (b.start_date<=TO_DATE(:END_DATE,'yyyymmddhh24miss')+1 or :END_DATE is null)