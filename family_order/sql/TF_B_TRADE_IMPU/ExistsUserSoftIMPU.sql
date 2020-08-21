select count(*) RECORDCOUNT 
  from tf_f_user_impu i
 where i.partition_id = mod(TO_NUMBER(:USER_ID),10000)
   and i.user_id = :USER_ID
   and i.rsrv_str1 = '2'
   and sysdate between i.start_date and i.end_date