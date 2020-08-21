SELECT SUM(NVL(integral_fee,0)) integral_fee
  FROM tf_ah_integralbill a,td_s_commpara b
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND integral_type_code=para_code1
   AND param_attr=:PARAM_ATTR
   AND param_code=:PARAM_CODE