select * FROM tf_f_account 
 WHERE acct_id=TO_NUMBER(:ACCT_ID) 
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000) 
