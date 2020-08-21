SELECT partition_id,
       to_char(user_id) user_id,
       to_char(user_id_a) user_id_a,
       discnt_code,
       spec_tag,
       relation_type_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_discnt
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id = TO_NUMBER(:USER_ID)
   and discnt_code = :DISCNT_CODE
   and start_date = TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')