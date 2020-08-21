UPDATE tf_f_user_newscore
   SET score=score + TO_NUMBER(:SCORE)  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(TO_NUMBER(:USER_ID), 10000)
   AND acyc_id=:ACYC_ID
   AND id_type=:ID_TYPE
   AND score_type_code=:SCORE_TYPE_CODE