UPDATE tf_f_user
   SET user_passwd=:USER_PASSWD
 WHERE user_id=:USER_ID
 AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)