SELECT partition_id,
       inst_id,
       to_char(user_id_a) user_id_a,
       serial_number_a,
       to_char(user_id_b) user_id_b,
       serial_number_b,
       relation_type_code,
       role_code_a,
       role_code_b,
       orderno,
       short_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_relation_bb
 WHERE user_id_a = TO_NUMBER(:USER_ID)
   AND end_date >= TRUNC(LAST_DAY(SYSDATE) + 1)
UNION
SELECT partition_id,
       inst_id,
       to_char(user_id_a) user_id_a,
       serial_number_a,
       to_char(user_id_b) user_id_b,
       serial_number_b,
       relation_type_code,
       role_code_a,
       role_code_b,
       orderno,
       short_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_relation_bb
 WHERE user_id_b = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND end_date >= TRUNC(LAST_DAY(SYSDATE) + 1)
