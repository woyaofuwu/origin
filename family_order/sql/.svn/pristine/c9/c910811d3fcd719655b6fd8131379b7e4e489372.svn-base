SELECT to_char(charge_id) charge_id,carrier_code,carrier_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,partition_id,eparchy_code 
  FROM tf_a_payother_log
 WHERE carrier_id=:CARRIER_ID and carrier_code=:CARRIER_CODE 
and operate_time>=to_date(:START_TIME,'YYYY-MM-DD HH24:MI:SS') and operate_time<=to_date(:END_TIME,'YYYY-MM-DD HH24:MI:SS')    and partition_id >= :PARTITION_ID 
   and partition_id <= :PARTITION_ID1