SELECT COUNT(1) recordcount
  FROM tf_f_user_otherserv
 WHERE partition_id = MOD(to_number(:USER_ID), 10000)
   AND user_id = to_number(:USER_ID)
   AND service_mode = '0'
   AND TRIM(process_tag) = '0'
   AND end_date > last_day(trunc(SYSDATE)) + 0.99999