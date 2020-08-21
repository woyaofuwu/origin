UPDATE tf_f_user
   SET remove_tag='0',destroy_time=NULL,pre_destroy_time=NULL,update_time=SYSDATE
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND remove_tag in ('1','2','3','4','5','6')