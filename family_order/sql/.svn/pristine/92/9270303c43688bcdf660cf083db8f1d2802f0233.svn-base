SELECT to_char(consign_id) consign_id,acyc_id,to_char(aspay_fee) aspay_fee,
to_char(acct_id) acct_id,recv_acyc_id,to_char(charge_id) charge_id
  FROM tf_a_consignlog
 WHERE mconsign_id=TO_NUMBER(:MCONSIGN_ID)
   AND nvl(return_tag,'0')=:RETURN_TAG