UPDATE tf_f_user_mbmp_sub

   SET end_date=sysdate,remark = :REMARK1,update_time=sysdate   

 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND biz_type_code=:BIZ_TYPE_CODE
   AND biz_state_code=:BIZ_STATE_CODE
   AND (remark = :REMARK OR :REMARK='*')
   AND sysdate BETWEEN start_date AND end_date