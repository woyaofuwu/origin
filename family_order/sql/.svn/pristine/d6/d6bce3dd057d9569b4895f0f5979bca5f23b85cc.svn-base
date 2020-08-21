UPDATE tf_f_user_other
   SET RSRV_STR1=:RSRV_STR1
 WHERE user_id=TO_NUMBER(:USER_ID)
 	AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND rsrv_value_code=:RSRV_VALUE_CODE