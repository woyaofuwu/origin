UPDATE sendsmssatisfy
   SET deal_flag = :DEAL_FLAG  
 WHERE serial_number = :SERIAL_NUMBER
   AND trunc(update_time) = trunc(sysdate)