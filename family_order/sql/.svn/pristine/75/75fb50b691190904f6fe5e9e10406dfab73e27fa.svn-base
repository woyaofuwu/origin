update tf_a_accesslog set cancel_tag='0' 
 where (operate_id = to_number(:OPERATE_ID)
   and partition_id >=:PARTITION_ID - 1
   and partition_id <=:PARTITION_ID + 1    
   and operate_type='1'
   and cancel_tag ='1' ) or 
   (operate_id = (select writeoff_id from tf_a_writeofflog where charge_id=:OPERATE_ID  
   and partition_id >=:PARTITION_ID - 1
   and partition_id <=:PARTITION_ID + 1 and rownum <=1)
   and operate_type='2'
   and cancel_tag ='1' )