DELETE FROM tf_f_relation_uu
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID_B), 10000)
   AND user_id_b = TO_NUMBER(:USER_ID_B)
   AND start_date >= end_date