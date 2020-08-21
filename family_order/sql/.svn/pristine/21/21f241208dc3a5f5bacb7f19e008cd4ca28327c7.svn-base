UPDATE tf_f_vpmn_grpout
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND serial_number=:SERIAL_NUMBER
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')