UPDATE tf_f_user_other
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
       update_time=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE rsrv_value_code=:RSRV_VALUE_CODE
   AND user_id=:USER_ID	
   AND rsrv_str2=:RSRV_STR2
   AND end_date>sysdate
   AND end_date>start_date