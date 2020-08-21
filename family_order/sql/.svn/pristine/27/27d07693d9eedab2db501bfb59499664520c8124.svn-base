DELETE FROM tf_f_user_other
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_value=:RSRV_VALUE
   AND rsrv_str1=:RSRV_STR1
   AND rsrv_str2=:RSRV_STR2
   AND rsrv_str10=:RSRV_STR10
   AND sysdate between start_date AND end_date