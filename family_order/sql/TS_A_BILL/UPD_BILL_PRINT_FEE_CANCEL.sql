UPDATE ts_a_bill
   SET print_fee=0
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND acyc_id>=:START_ACYC_ID
   AND acyc_id<=:END_ACYC_ID