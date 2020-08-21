DELETE FROM tf_a_invoicetype
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND start_acyc_id=:START_ACYC_ID