SELECT COUNT(*) recordcount
  FROM tf_f_user_otherserv
 WHERE user_id=:USER_ID
   AND partition_id=mod(:USER_ID,10000)
   AND service_mode in ('u1','u2')
   AND process_tag='0'
   AND end_date>sysdate