UPDATE tf_f_user_other
   SET rsrv_str2=to_char(to_number(nvl(rsrv_str2,0))+1)
   WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND rsrv_value_code = :RSRV_VALUE_CODE
   AND rsrv_value = :rsrv_value
  AND rsrv_str1 = :RSRV_STR1
   AND SYSDATE BETWEEN start_date AND end_date+0