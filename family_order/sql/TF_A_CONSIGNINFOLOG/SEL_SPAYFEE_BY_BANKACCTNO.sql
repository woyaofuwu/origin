SELECT to_char(acct_id) acct_id,acyc_id,to_char(SUM(aspay_fee)) aspay_fee 
  FROM tf_a_consigninfolog
WHERE bank_acct_no=:BANK_ACCT_NO AND recv_acyc_id =:RECV_ACYC_ID AND commit_tag = '0'
GROUP BY acct_id,acyc_id ORDER BY acct_id,acyc_id