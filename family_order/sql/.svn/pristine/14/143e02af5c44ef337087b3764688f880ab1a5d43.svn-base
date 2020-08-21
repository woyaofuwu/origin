UPDATE tf_f_user
   SET remove_tag=:REMOVE_TAG,destroy_time=NULL,update_time=SYSDATE  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id=TO_NUMBER(:USER_ID)