UPDATE tf_f_user_newscore
   SET score=nvl(score,0)+TO_NUMBER(:SCORE),rsrv_num1=nvl(rsrv_num1,0)+to_number(:RSRV_NUM1)/100
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND year_id=TO_NUMBER(:YEAR_ID)
   AND id_type='0'
   AND score_type_code IN ('02','03')