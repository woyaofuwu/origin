UPDATE tf_f_user_discnt 
   SET end_date=SYSDATE,update_time=sysdate
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND spec_tag='2'
   AND end_date>SYSDATE