UPDATE tf_f_user_mbmp
   SET end_date=sysdate 
   WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND biz_type_code=:BIZ_TYPE_CODE
   AND biz_state_code=:BIZ_STATE_CODE
   AND sysdate BETWEEN start_date+0 AND end_date+0