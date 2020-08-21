UPDATE tf_f_user_other
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND (rsrv_str1=:RSRV_STR1 OR rsrv_str1 IS NULL)
   AND rsrv_str2=:RSRV_STR2
   AND rsrv_str3=:RSRV_STR3
   AND ((SYSDATE BETWEEN start_date AND end_date)
        OR 
       (start_date > SYSDATE   AND   end_date> SYSDATE  AND start_date < end_date ))