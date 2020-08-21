SELECT to_char(e.trade_id) trade_id,
       e.accept_month,
       to_char(e.user_id) user_id,
       e.service_id,
       e.serial_number,
       e.info_code,
       e.info_value,
       e.info_name,
       to_char(e.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       e.update_staff_id,
       e.update_depart_id,
       e.remark,
       to_char(e.rsrv_num1) rsrv_num1,
       to_char(e.rsrv_num2) rsrv_num2,
       e.rsrv_str1,
       e.rsrv_str2,
       to_char(e.rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(e.rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(e.rsrv_date3, 'yyyy-mm-dd hh24:mi:ss') rsrv_date3
  from TF_B_TRADE_PLATSVC_ATTR e,TF_B_TRADE_PLATSVC s
 where e.service_id = s.service_id
    and e.trade_id = s.trade_id 
   and	e.trade_id = :TRADE_ID
   and e.info_code = :INFO_CODE
   AND e.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))