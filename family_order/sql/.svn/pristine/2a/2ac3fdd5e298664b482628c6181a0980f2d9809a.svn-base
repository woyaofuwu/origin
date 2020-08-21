SELECT to_char(operate_id) operate_id,operate_type,partition_id,to_char(acct_id) acct_id,deposit_code,access_tag,to_char(money) money,to_char(old_balance) old_balance,to_char(new_balance) new_balance,cancel_tag,eparchy_code,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time 
  FROM tf_a_accesslog
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND cancel_tag='0'
   AND operate_time+0>TO_DATE(:OPERATE_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND rownum < 2
union all 
SELECT to_char(operate_id) operate_id,operate_type,partition_id,to_char(acct_id) acct_id,deposit_code,access_tag,to_char(money) money,to_char(old_balance) old_balance,to_char(new_balance) new_balance,cancel_tag,eparchy_code,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time 
  FROM tf_a_accesslog_d
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND cancel_tag='0'
   AND operate_time+0>TO_DATE(:OPERATE_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND rownum < 2