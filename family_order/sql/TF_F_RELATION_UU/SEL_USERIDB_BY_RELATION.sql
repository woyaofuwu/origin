select user_id_b
  from tf_f_relation_uu
 where user_id_a = (select user_id_a
                      from tf_f_relation_uu
                     where user_id_b = :USER_ID_B
                       and relation_type_code = :RELATION_TYPE_CODE
                       AND end_date > sysdate)
   AND role_code_b = :ROLE_CODE_B    AND end_date > sysdate