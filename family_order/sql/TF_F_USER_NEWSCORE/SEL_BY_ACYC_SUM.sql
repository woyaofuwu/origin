SELECT :USER_ID user_id,MOD(TO_NUMBER(:USER_ID),10000) partition_id,'' year_id,0 acyc_id,:ID_TYPE id_type,
:SCORE_TYPE_CODE score_type_code,to_char(sum(nvl(score,0))) score,'' rsrv_str1,'' rsrv_str2,'' rsrv_num1,'' rsrv_num2,'' rsrv_date1,'' rsrv_date2 
  FROM tf_f_user_newscore
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND id_type=:ID_TYPE
   AND score_type_code=:SCORE_TYPE_CODE
   AND acyc_id between :START_ACYC and :END_ACYC
   AND score> 0