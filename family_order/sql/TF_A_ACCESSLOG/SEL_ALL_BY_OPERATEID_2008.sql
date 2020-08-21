SELECT to_char(operate_id) operate_id,operate_type,partition_id,to_char(acct_id) acct_id,deposit_code,access_tag,to_char(money) money,to_char(old_balance) old_balance,to_char(new_balance) new_balance,cancel_tag,eparchy_code,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time 
  FROM tf_ahb_accesslog_2008
 WHERE operate_id=TO_NUMBER(:OPERATE_ID)
   AND operate_type=:OPERATE_TYPE
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
union all
SELECT to_char(operate_id) operate_id,operate_type,partition_id,to_char(acct_id) acct_id,deposit_code,access_tag,to_char(money) money,to_char(old_balance) old_balance,to_char(new_balance) new_balance,cancel_tag,eparchy_code,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time 
  FROM tf_ahb_accesslog_d_2008
 WHERE operate_id=TO_NUMBER(:OPERATE_ID)
   AND (:OPERATE_TYPE||'aaa'!='bbb')
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID