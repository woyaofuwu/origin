SELECT partition_id,
       to_char(user_id) user_id,
       discnt_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  from tf_f_user_discnt
 where user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND DISCNT_CODE ='88888889'
   AND SYSDATE BETWEEN START_DATE AND END_DATE