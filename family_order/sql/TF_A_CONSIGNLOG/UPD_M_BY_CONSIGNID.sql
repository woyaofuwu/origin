UPDATE tf_a_consignlog
   SET mconsign_id=TO_NUMBER(:MCONSIGN_ID)  
 WHERE consign_id=TO_NUMBER(:CONSIGN_ID)
   AND recv_acyc_id=:RECV_ACYC_ID