SELECT to_char(acct_id) acct_id,partition_id,acyc_id,bcyc_id,to_char(all_money) all_money,to_char(all_balance) all_balance,rsrv_str1,rsrv_str2,rsrv_str3,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_a_leavereal_month
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id= TO_NUMBER(:PARTITION_ID)
   AND acyc_id=:ACYC_ID