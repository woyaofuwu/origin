SELECT partition_id,
       to_char(user_id) user_id_b,
       to_char(user_id_a) user_id_a,
       discnt_code,
       spec_tag,
       relation_type_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  from tf_f_user_discnt
 WHERE user_id_a = TO_NUMBER(:USER_ID_A)
   AND spec_tag = '2'
   AND end_date > SYSDATE