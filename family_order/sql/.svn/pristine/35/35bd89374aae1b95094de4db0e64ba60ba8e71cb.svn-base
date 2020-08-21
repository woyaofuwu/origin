UPDATE tf_f_user
   SET user_passwd=:USER_PASSWD
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)