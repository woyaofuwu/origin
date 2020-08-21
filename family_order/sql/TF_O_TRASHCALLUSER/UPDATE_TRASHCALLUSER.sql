UPDATE TF_O_TRASHCALLUSER
   SET end_date=sysdate  
 WHERE serial_number=:SERIAL_NUMBER
   AND state=:STATE