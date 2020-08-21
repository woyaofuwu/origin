UPDATE tf_f_user_svc
   SET destroy_tag=:DESTROY_TAG,update_time=sysdate
 WHERE user_id=:USER_ID
AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND destroy_tag='0'
   AND end_date>sysdate