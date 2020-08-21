select * from tf_f_relation_uu
WHERE  user_id_b=TO_NUMBER(:USER_ID_B)
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   and end_date > sysdate