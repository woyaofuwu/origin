INSERT INTO tf_a_accesslog_rc(operate_id,operate_type,partition_id,acct_id,deposit_code,access_tag,money,old_balance,new_balance,cancel_tag,eparchy_code,operate_time) 
SELECT operate_id,operate_type,partition_id,acct_id,deposit_code,access_tag,money,old_balance,new_balance,cancel_tag,eparchy_code,operate_time
  FROM tf_a_accesslog
 WHERE operate_id=TO_NUMBER(:OPERATE_ID)
   AND operate_type=:OPERATE_TYPE
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'