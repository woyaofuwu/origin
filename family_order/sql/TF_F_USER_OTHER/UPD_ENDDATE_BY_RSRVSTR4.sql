UPDATE tf_f_user_other
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str4=:RSRV_STR4
   AND end_date>sysdate