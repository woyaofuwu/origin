DELETE FROM tf_f_user_infochange a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(to_number(:USER_ID), 10000)
   AND a.end_date >= SYSDATE