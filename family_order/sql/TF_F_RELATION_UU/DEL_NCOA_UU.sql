UPDATE tf_f_relation_uu
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
     update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),
   update_staff_id=:UPDATE_STAFF_ID,
   update_depart_id=:UPDATE_DEPART_ID  
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND end_date>sysdate