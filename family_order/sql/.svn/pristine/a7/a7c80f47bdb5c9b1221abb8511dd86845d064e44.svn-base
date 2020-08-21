UPDATE tf_f_user_other
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND (start_date=TO_DATE(:START_DATE , 'YYYY-MM-DD HH24:MI:SS') or  :START_DATE is null or :START_DATE='')
   AND sysdate < end_date+0