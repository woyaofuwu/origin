SELECT acct_id,acyc_id,sum(nvl(aspay_fee,0)) aspay_fee
  FROM tf_a_consigninfolog
 WHERE recv_acyc_id=:RECV_ACYC_ID
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND commit_tag='0'
   GROUP BY acct_id,acyc_id
   ORDER BY acyc_id