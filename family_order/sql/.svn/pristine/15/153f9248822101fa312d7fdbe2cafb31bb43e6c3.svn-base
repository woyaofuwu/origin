SELECT COUNT(1) recordcount
  FROM tf_f_user_other
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND start_date>=to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')