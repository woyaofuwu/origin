UPDATE tf_f_accountdeposit
   SET start_acyc_id=:START_ACYC_ID,end_acyc_id=:END_ACYC_ID  
 WHERE 
   acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND deposit_code=:DEPOSIT_CODE