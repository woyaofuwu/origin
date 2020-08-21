SELECT to_char(charge_id) charge_id,carrier_code,carrier_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,partition_id,eparchy_code 
  FROM tf_a_payother_log
 WHERE carrier_code=:CARRIER_CODE
   AND carrier_id=:CARRIER_ID
   AND (eparchy_code=:EPARCHY_CODE or :EPARCHY_CODE is null)