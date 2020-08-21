SELECT partition_id,to_char(user_id) user_id,integral_type_code,integral_fee,acyc_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM tf_ah_integralbill
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND integral_type_code=:SCORE_TYPE_CODE or (:SCORE_TYPE_CODE='-1')
   AND acyc_id BETWEEN :START_ACYC_ID AND :END_ACYC_ID