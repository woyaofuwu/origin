select *
  from tf_f_user_impu v
 where v.partition_id = MOD(to_number(:USER_ID), 10000)
   and v.user_id = to_number(:USER_ID)
   and v.RSRV_STR1 = '0'
   and sysdate between v.start_date and v.end_date
