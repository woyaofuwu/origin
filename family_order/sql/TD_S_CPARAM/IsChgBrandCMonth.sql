SELECT COUNT(1) recordcount
  FROM tf_f_user_brandchange
 WHERE user_id=:USER_ID
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND brand_no=:RSRV_STR1
   AND SYSDATE BETWEEN start_date AND end_date