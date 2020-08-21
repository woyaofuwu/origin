UPDATE tf_f_user_file
   SET remove_tag=:REMOVE_TAG,destroy_time=TO_DATE(:DESTROY_TIME, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id=TO_NUMBER(:USER_ID)
 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)