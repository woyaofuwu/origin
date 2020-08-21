INSERT INTO tf_a_paylog_rc(charge_id,partition_id,eparchy_code,city_code,cust_id,user_id,acct_id,charge_source_code,pay_fee_mode_code,recv_fee,deposit_code,cancel_tag,recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id) 
SELECT charge_id,partition_id,eparchy_code,city_code,cust_id,user_id,acct_id,charge_source_code,pay_fee_mode_code,recv_fee,deposit_code,cancel_tag,recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id
FROM tf_a_paylog
WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'