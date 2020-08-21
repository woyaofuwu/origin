update tf_f_user_acctday
  set  end_date  = to_date('2050-12-31','YYYY-MM-DD hh24:mi:ss'),
       UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID = :UPDATE_DEPART_ID,
       update_time = sysdate
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND start_date=
   (select max(a.start_date) from tf_f_user_acctday a
    where a.user_id = TO_NUMBER(:USER_ID)
      and a.partition_id=mod(TO_NUMBER(:USER_ID),10000))