update tf_a_writeofflog set cancel_tag='0'
   where charge_id=to_number(:CHARGE_ID)
    and partition_id >=:PARTITION_ID - 1
    and partition_id <=:PARTITION_ID + 1   
    and cancel_tag='1'