UPDATE tf_f_user_score_new
   SET score = :SCORE,score_value = :SCORE_VALUE
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND brand_code = :BRAND_CODE
   AND bcyc_id = :BCYC_ID
   AND score_type_code = :SCORE_TYPE_CODE