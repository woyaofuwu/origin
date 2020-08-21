SELECT user_id FROM tf_f_user 
WHERE open_mode=1
AND user_id = TO_NUMBER(:USER_ID)
AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)