SELECT COUNT(1) recordcount
  FROM tf_f_user_other
 WHERE RSRV_VALUE=:RSRV_STR4
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND sysdate BETWEEN start_date+0 AND end_date+0