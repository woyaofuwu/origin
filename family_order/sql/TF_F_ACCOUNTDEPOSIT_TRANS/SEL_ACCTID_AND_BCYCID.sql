SELECT eparchy_code,partition_id,to_char(acct_id) acct_id,deposit_code,bcyc_id,to_char(money) money,start_acyc_id,end_acyc_id,to_char(input_time,'yyyy-mm-dd hh24:mi:ss') input_time,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_tag 
  FROM tf_f_accountdeposit_trans
WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND bcyc_id=:BCYC_ID
   AND deposit_code=:DEPOSIT_CODE