delete tf_f_account_consign
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND START_CYCLE_ID>END_CYCLE_ID