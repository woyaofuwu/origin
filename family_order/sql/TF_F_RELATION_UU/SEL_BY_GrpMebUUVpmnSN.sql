SELECT a.user_id_a
  FROM tf_f_relation_uu a, tf_f_user b
 WHERE remove_tag = '0'
   AND a.user_id_b = b.user_id
   AND a.partition_id = mod(b.user_id, 10000)
   AND b.partition_id = mod(b.user_id, 10000)
   AND a.start_date < a.end_date
   AND a.relation_type_code = '20'
   AND b.serial_number = :SERIAL_NUMBER
   AND a.end_date > sysdate