SELECT a.partition_id,
       to_char(a.user_id) user_id,
       a.rsrv_value_code,
       a.rsrv_value,
       a.rsrv_str1,
       a.rsrv_str2,
       a.rsrv_str3,
       a.rsrv_str4,
       a.rsrv_str5,
       a.rsrv_str6,
       a.rsrv_str7,
       a.rsrv_str8,
       b.biz_name RSRV_STR9,
       c.sp_name RSRV_STR10,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_other a, Td_m_Operation_Sp b, Td_m_Corporation_Sp c
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.rsrv_value_code = :RSRV_VALUE_CODE
   AND a.rsrv_str4 = b.sp_code(+)
   AND a.rsrv_str3 = trim(b.biz_code(+))
   AND a.rsrv_str4 = c.sp_code
   AND sysdate BETWEEN a.start_date + 0 AND a.end_date + 0