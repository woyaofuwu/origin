DELETE FROM tf_f_user_svc a
 WHERE a.partition_id = MOD(to_number(:USER_ID), 10000)
   AND a.user_id = to_number(:USER_ID)