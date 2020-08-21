DELETE FROM tf_f_user_score
 WHERE user_id=TO_NUMBER(:USER_ID)
AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)