UPDATE tf_a_consigninfolog
   SET MCONSIGN_ID=TO_NUMBER(:MCONSIGN_ID)
 WHERE acct_id=TO_NUMBER(:ACCT_ID) AND
   bill_id=TO_NUMBER(:BILL_ID)
   AND recv_acyc_id=:RECV_ACYC_ID
   AND eparchy_code=:EPARCHY_CODE