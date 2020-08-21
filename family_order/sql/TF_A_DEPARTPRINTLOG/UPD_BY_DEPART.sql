UPDATE tf_a_departprintlog
   SET trans_money=TO_NUMBER(:TRANS_MONEY),
   print_times = print_times + 1,
   print_time = sysdate,
   print_eparchy_code=:PRINT_EPARCHY_CODE,
   print_city_code=:PRINT_CITY_CODE,
   print_depart_id=:PRINT_DEPART_ID,
   print_staff_id=:PRINT_STAFF_ID  
 WHERE depart_id=:DEPART_ID
   AND depart_log_time=TO_DATE(:DEPART_LOG_TIME, 'YYYYMMDD')