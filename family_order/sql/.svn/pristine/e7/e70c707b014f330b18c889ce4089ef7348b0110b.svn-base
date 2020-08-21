UPDATE tf_f_user_discnt a
   SET a.end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-0.00001,a.update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
 WHERE a.partition_id=mod(to_number(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND a.end_date > sysdate