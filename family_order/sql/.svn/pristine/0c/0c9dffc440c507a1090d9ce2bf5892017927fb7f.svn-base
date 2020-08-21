SELECT partition_id,to_char(user_id) user_id,to_char(charge_id) charge_id,to_char(bill_id) bill_id 
  FROM tf_a_producebilllog
 WHERE partition_id=:PARTITION_ID
   AND charge_id=TO_NUMBER(:CHARGE_ID)