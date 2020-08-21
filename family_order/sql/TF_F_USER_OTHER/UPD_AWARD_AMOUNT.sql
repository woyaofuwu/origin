UPDATE tf_f_user_other
   SET rsrv_str2=to_char(to_number(rsrv_str2)-to_number(:RSRV_STR2))  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str1=:RSRV_STR1
   AND sysdate < end_date+0