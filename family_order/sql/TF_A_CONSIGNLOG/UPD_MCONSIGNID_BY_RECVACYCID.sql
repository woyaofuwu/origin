UPDATE tf_a_consignlog
   SET mconsign_id=TO_NUMBER(:MCONSIGN_ID)  
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND acyc_id=:ACYC_ID
   AND recv_acyc_id=:RECV_ACYC_ID