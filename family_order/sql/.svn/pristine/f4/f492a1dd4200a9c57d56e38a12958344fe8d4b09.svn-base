select *
  from tf_f_user_impu v
 where 1=1
   and v.partition_id = MOD(to_number(:USER_ID), 10000)
   and v.user_id = to_number(:USER_ID)
   and (v.RSRV_STR1 = '0' or (v.RSRV_STR1 = '1' and v.RSRV_STR5 = '1'))
   and sysdate between v.start_date and v.end_date
