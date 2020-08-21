update tf_f_relation_uu a
   set serial_number_b = :SERIAL_NUMBER , user_id_b =TO_NUMBER(:USER_ID_B)
 where user_id_b = TO_NUMBER(:USER_ID)
   and relation_type_code = :RELATION_TYPE_CODE
   and end_date > sysdate