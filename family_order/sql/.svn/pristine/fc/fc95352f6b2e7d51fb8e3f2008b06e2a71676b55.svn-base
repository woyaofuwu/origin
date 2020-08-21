DELETE tf_f_user_other
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str1=:RSRV_STR1 OR :RSRV_STR1 IS NULL
   AND rsrv_str2=:RSRV_STR2 OR :RSRV_STR2 IS NULL
   AND rsrv_str3=:RSRV_STR3 OR :RSRV_STR3 IS NULL
   AND (sysdate BETWEEN start_date+0 AND end_date+0)