select max(start_date) START_DATE
  From tf_f_user_svcstate
 where service_id = '0'
   and user_id = :USER_ID
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)