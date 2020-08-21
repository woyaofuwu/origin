UPDATE tf_f_user_other
   SET RSRV_STR9=:RSRV_STR9,RSRV_STR10=:RSRV_STR10
 WHERE user_id=TO_NUMBER(:USER_ID)
 	AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND (rsrv_value = :RSRV_VALUE or :RSRV_VALUE is null)
   AND end_date > sysdate