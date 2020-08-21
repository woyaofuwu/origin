SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,service_id,main_tag,
       to_char(inst_id) inst_id, to_char(campn_id) campn_id,to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_svc
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND SYSDATE BETWEEN start_date+0 AND end_date+0
   AND service_id IN ('16','17','18','19')
