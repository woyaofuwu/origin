SELECT to_char(acct_id) acct_id, acyc_id, to_char(sum(aspay_fee)) aspay_fee
  FROM tf_a_consigninfolog
 WHERE mconsign_id = TO_NUMBER(:MCONSIGN_ID)
   AND eparchy_code = :EPARCHY_CODE AND COMMIT_TAG='0'
 GROUP BY acct_id, acyc_id
 order by acct_id, acyc_id