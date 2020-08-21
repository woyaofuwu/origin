SELECT to_char(acct_id) acct_id,sum(nvl(ASPAY_FEE,0) + nvl(AIMP_FEE,0))  ASPAY_FEE 
  FROM tf_a_consigninfolog
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND acyc_id=:ACYC_ID
   AND commit_tag='0'
   group by acct_id