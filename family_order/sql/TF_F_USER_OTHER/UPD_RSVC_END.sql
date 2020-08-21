UPDATE tf_f_user_other
   SET rsrv_str10=:RSRV_STR10,end_date=sysdate 
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str2=:RSRV_STR2
   AND end_date>sysdate