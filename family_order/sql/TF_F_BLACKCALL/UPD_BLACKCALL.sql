UPDATE tf_f_blackcall
   SET end_date=SYSDATE  
 WHERE serial_number=:SERIAL_NUMBER
   AND state=:STATE
   AND end_date>sysdate