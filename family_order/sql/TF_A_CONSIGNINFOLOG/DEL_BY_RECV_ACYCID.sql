DELETE FROM tf_a_consigninfolog
 WHERE recv_acyc_id=:RECV_ACYC_ID
   AND acct_id=TO_NUMBER(:ACCT_ID)