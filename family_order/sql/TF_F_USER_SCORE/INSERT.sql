INSERT INTO tf_f_user_score(partition_id,user_id,score_type_code,score_value)
 VALUES(MOD(:USER_ID,10000),TO_NUMBER(:USER_ID),:SCORE_TYPE_CODE,TO_NUMBER(:SCORE_VALUE))