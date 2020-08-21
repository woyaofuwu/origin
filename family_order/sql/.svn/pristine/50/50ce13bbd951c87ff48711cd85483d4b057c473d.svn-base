UPDATE tf_f_user
   SET remove_tag=:REMOVE_TAG,destroy_time=sysdate,
       remove_eparchy_code=:REMOVE_EPARCHY_CODE,remove_city_code=:REMOVE_CITY_CODE,
       remove_depart_id=:REMOVE_DEPART_ID,remove_reason_code=:REMOVE_REASON_CODE,
       remark=:REMARK  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)