select * 
  from tf_f_user_platsvc a
 where a.partition_id = mod(:USER_ID, 10000)
   and a.user_id = :USER_ID
   and a.biz_state_code in ('A', 'E')
   and to_number(to_char(sysdate, 'yyyymm')) =
       to_number(to_char(a.start_date, 'yyyymm'))
