SELECT to_char(charge_id) charge_id,partition_id,eparchy_code,city_code,to_char(cust_id) cust_id,to_char(user_id) user_id,to_char(acct_id) acct_id,charge_source_code,pay_fee_mode_code,to_char(recv_fee) recv_fee,deposit_code,cancel_tag,to_char(recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,to_char(cancel_time,'yyyy-mm-dd hh24:mi:ss') cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id  
  FROM tf_a_paylog
 WHERE acct_id=TO_NUMBER(:ACCT_ID) AND partition_id>=:START_PARTITION_ID 
     AND partition_id <=:END_PARTITION_ID 
 AND  recv_time >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
 AND recv_time <=TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss') 
AND CHARGE_SOURCE_CODE not IN 
(
 SELECT TAG_NUMBER FROM TD_S_TAG 
 WHERE TAG_CODE LIKE 'ASM_NOT_EFFECT_PAYLOG%' AND USE_TAG='1'
)