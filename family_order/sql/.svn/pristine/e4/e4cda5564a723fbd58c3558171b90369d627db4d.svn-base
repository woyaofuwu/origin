UPDATE tf_f_user_discnt 
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
    update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),
   update_staff_id=:UPDATE_STAFF_ID,
   update_depart_id=:UPDATE_DEPART_ID    
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND spec_tag='2'
   AND discnt_code=TO_NUMBER(:DISCNT_CODE)
   AND end_date>SYSDATE