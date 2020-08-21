SELECT to_char(acct_id) acct_id,acyc_id,to_char(SUM(aspay_fee)) aspay_fee 
  FROM tf_a_consigninfolog
WHERE mod(acct_id,10000) >=:START_ID AND mod(acct_id,10000) <=:END_ID
AND recv_acyc_id =:RECV_ACYC_ID AND commit_tag = '0'
GROUP BY acct_id,acyc_id ORDER BY acct_id,acyc_id