SELECT COUNT(1) SUM_COUNT 
  FROM tf_f_relation_uu
 WHERE user_id_a = TO_NUMBER(:USER_ID_A)
    AND relation_type_code = :RELATION_TYPE_CODE
    AND end_date > start_date
    AND end_date > sysdate  