UPDATE tf_f_user_otherserv
set user_id = user_id + 100000000000000
where partition_id=mod(to_number(:USER_ID),10000)
  and user_id=to_number(:USER_ID)
  and service_mode='FG'
  and rsrv_num1<>20
  and process_tag='0'
  --and sysdate between start_date and end_date