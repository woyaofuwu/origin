UPDATE tf_f_vpmn_memberout
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND user_id_b=TO_NUMBER(:USER_ID_B)
   AND serial_number=:SERIAL_NUMBER
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')