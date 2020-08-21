SELECT user_id,
       	year_id,
       	acyc_id,
       	id_type,
       	score_type_code,
       	score,
      	rsrv_str1,
      	rsrv_str2,
        rsrv_num1,
        rsrv_num2,
        rsrv_date1,
        rsrv_date2
  FROM tf_f_user_newscore
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID), 1000)
   AND year_id=:YEAR_ID
   AND (id_type=:ID_TYPE OR :ID_TYPE = '*')
   AND (score_type_code=:SCORE_TYPE_CODE OR :SCORE_TYPE_CODE = '**')