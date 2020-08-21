UPDATE tf_f_relation_uu
   SET end_date=sysdate  
 WHERE user_id_b=TO_NUMBER(:USER_ID_B)
   AND partition_id=mod(user_id_b,10000)
   AND end_date>sysdate