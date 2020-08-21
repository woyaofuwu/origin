SELECT to_char(acct_id) acct_id ,sum(nvl(ASPAY_FEE,0) + nvl(AIMP_FEE,0))  ASPAY_FEE 
  FROM tf_a_consigninfolog
 WHERE acyc_id=:ACYC_ID
   AND vip_id=TO_NUMBER(:VIP_ID)
   AND commit_tag='0'
   group by acct_id