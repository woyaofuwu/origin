UPDATE tf_f_user_plat_order
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600,remark=:REMARK  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND biz_state_code = :OLD_STATE_CODE
   AND start_date >= TO_DATE(:OLD_START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND end_date > SYSDATE