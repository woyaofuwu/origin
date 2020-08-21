UPDATE tf_a_subconsignlog
   SET mconsign_id=TO_NUMBER(:MCONSIGN_ID)  
 WHERE consign_id=TO_NUMBER(:CONSIGN_ID)
   AND recv_acyc_id+0=:RECV_ACYC_ID