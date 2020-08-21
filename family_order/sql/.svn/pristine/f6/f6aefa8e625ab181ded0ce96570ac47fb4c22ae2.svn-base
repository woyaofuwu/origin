UPDATE tf_f_user_plat_register
   SET end_date=TO_DATE(:DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600,
       remark=:REMARK,
       update_time=SYSDATE  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN start_date+0 AND end_date+0