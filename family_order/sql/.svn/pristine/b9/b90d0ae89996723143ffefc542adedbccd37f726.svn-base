SELECT to_char(charge_id) charge_id,carrier_code,carrier_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,partition_id,eparchy_code 
  FROM tf_a_payother_log_rc
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'