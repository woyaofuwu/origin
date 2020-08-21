SELECT to_char(acct_id) acct_id,acyc_id,recv_acyc_id,to_char(SUM(aspay_fee)) aspay_fee 
  FROM tf_a_consigninfolog
WHERE mconsign_id=TO_NUMBER(:MCONSIGN_ID)
AND commit_tag = '0'
GROUP BY acct_id,acyc_id,recv_acyc_id ORDER BY acct_id,acyc_id,recv_acyc_id