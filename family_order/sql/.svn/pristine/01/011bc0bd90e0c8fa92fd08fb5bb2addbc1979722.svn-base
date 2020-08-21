SELECT COUNT(1) recordcount
  FROM tf_f_user_other
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND (:RSRV_STR6 is null OR rsrv_str2=:RSRV_STR6)
   AND sysdate BETWEEN start_date+0 AND end_date+0