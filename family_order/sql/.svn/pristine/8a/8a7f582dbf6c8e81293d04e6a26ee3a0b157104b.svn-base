UPDATE tf_f_vpmngroup
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
 AND user_id_b=TO_NUMBER(:USER_ID_B)
 AND end_date>sysdate