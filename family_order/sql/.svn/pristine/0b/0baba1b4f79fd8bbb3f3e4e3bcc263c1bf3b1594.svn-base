SELECT 1 partition_id,1 user_id,0 score_type_code,to_char(sum(score_value)) score_value
FROM tf_f_user_score
 WHERE user_id=TO_NUMBER(:USER_ID)
AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)