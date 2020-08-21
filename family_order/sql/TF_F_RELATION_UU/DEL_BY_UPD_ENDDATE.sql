UPDATE tf_f_relation_uu
   SET end_date = sysdate
 WHERE partition_id = :PARTITION_ID
   AND user_id_b = TO_NUMBER(:USER_ID_B)