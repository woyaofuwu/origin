select *
   from TF_F_RELATION_UU
  where user_id_a in (select user_id_a
                        from TF_F_RELATION_UU
                       where user_id_b = :USER_ID_B and end_date > sysdate)
    and relation_type_code = :RELATION_TYPE_CODE
    and role_code_b = :ROLE_CODE_B
    and end_date > sysdate