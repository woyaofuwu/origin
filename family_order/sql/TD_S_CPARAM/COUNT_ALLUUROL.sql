SELECT count(*) recordcount
  FROM tf_f_relation_uu
 WHERE (user_id_a=TO_NUMBER(:USER_ID_B) OR user_id_b=TO_NUMBER(:USER_ID_B))
   AND relation_type_code like '%'||:RELATION_TYPE_CODE||'%'
   AND SYSDATE BETWEEN start_date AND end_date