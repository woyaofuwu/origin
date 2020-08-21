select *
   from tf_f_relation_uu
  where user_id_a = (select user_id_a
                       from tf_f_relation_uu
                      where user_id_b = :USER_ID
                        and RELATION_TYPE_CODE = :RELATION_TYPE_CODE AND end_date > SYSDATE) AND end_date > SYSDATE order by role_code_b