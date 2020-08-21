UPDATE tf_f_user_discnt 
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND spec_tag='2'
   AND end_date>SYSDATE