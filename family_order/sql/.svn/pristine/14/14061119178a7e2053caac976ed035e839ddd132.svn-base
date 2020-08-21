SELECT partition_id,to_char(user_id) user_id,integral_type_code,b.SCORE_TYPE_NAME,
integral_fee,acyc_id
  FROM tf_ah_integralbill a, td_s_scoretype b
 WHERE user_id=TO_NUMBER(:USER_ID)       
    AND  partition_id=mod(TO_NUMBER(:USER_ID) ,10000)
   AND acyc_id=:ACYC_ID
   AND a.integral_type_code = b.SCORE_TYPE_CODE