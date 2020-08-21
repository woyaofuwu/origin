UPDATE tf_f_user_newscore
   SET score=score+nvl(rsrv_num2,0) 
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND year_id=:YEAR_ID
   AND (id_type=:ID_TYPE OR :ID_TYPE = '*')
   AND (score_type_code=:SCORE_TYPE_CODE OR :SCORE_TYPE_CODE = '**')