update tf_a_paylog set cancel_tag = '0',cancel_time = null,cancel_eparchy_code = null,
	cancel_city_code = null,cancel_depart_id = null,cancel_staff_id = null
  where charge_id = to_number(:CHARGE_ID) 
   and partition_id >=:PARTITION_ID-1
   and partition_id <=:PARTITION_ID+1           
   and cancel_tag = '1'