UPDATE tf_a_writeofflog
   SET cancel_tag='1'  
 WHERE writeoff_id=TO_NUMBER(:WRITEOFF_ID)
   AND partition_id= :PARTITION_ID
   AND cancel_tag = '0'