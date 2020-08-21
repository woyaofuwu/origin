INSERT INTO tf_f_user_score(partition_id,user_id,score_type_code,score_value)
 select MOD(TO_NUMBER(:USER_ID_A),10000),TO_NUMBER(:USER_ID_A),score_type_code,score_value
   from tf_f_user_score
  where user_id=:USER_ID_B
    and partition_id=MOD(TO_NUMBER(:USER_ID_B),10000)