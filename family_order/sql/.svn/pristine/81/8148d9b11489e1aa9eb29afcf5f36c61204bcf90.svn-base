SELECT NVL(MAX(acyc_id),-1) acyc_id
FROM tf_f_user_newscore
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND id_type=:ID_TYPE
   AND score_type_code=:SCORE_TYPE_CODE