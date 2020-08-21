UPDATE tf_a_writeofflog
   SET cancel_tag='1'  
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id= :PARTITION_ID
   AND cancel_tag = '0'