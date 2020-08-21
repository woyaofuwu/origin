UPDATE tf_f_user_other
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND sysdate < end_date+0