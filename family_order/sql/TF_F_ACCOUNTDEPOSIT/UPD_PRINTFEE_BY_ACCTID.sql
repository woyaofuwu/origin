UPDATE tf_f_accountdeposit
   SET outprint_fee=outprint_fee+TO_NUMBER(:OUTPRINT_FEE)  
 WHERE acct_id=TO_NUMBER(:ACCT_ID) AND partition_id=MOD(:PARTITION_ID,10000) AND DEPOSIT_CODE=0