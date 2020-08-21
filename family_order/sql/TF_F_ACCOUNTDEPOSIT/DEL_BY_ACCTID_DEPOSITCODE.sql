DELETE FROM tf_f_accountdeposit
 WHERE partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND deposit_code=:DEPOSIT_CODE