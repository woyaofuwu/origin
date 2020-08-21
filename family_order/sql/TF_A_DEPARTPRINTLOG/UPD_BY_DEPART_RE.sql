UPDATE tf_a_departprintlog
   SET print_info1=:PRINT_INFO1,
   print_number1=TO_NUMBER(:PRINT_NUMBER1)  
 WHERE depart_id=:DEPART_ID
   AND depart_log_time=TO_DATE(:DEPART_LOG_TIME, 'YYYYMMDD')