UPDATE tf_a_consigninfolog
   SET charge_id=TO_NUMBER(:CHARGE_ID), 
       commit_tag='1'
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND acyc_id=:ACYC_ID 
   AND recv_acyc_id+0=:RECV_ACYC_ID
   AND commit_tag='0'