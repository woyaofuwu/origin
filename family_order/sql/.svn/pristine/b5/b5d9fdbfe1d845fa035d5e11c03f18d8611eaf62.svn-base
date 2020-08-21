SELECT a.user_id_a,
       a.serial_number_a,
       a.user_id_b,
       a.serial_number_b,
       a.relation_type_code,
       a.role_code_a,
       a.role_code_b,
       a.orderno,
       a.start_date,
       a.end_date
  FROM tf_f_relation_uu a
 WHERE a.relation_type_code = :RELATION_TYPE_CODE
   AND a.role_code_b = :ROLE_CODE_B
   AND a.user_id_a IN (SELECT b.user_id_a
                         FROM tf_f_relation_uu b
                        WHERE b.relation_type_code = :RELATION_TYPE_CODE
                          AND b.user_id_b = to_number(:USER_ID_B)
                          AND b.role_code_b = '1'
                          AND b.end_date > SYSDATE)
   AND a.end_date > sysdate