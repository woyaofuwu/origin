SELECT COUNT(1) recordcount
  FROM tf_f_user
 WHERE partition_id = MOD(to_number(:USER_ID), 10000)
   AND user_id = to_number(:USER_ID)
   AND open_mode='2'