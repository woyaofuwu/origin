UPDATE tf_f_accountdeposit_trans
   SET deal_time=SYSDATE,deal_tag='1'
 WHERE  acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND deposit_code=:DEPOSIT_CODE
   AND bcyc_id=:BCYC_ID
   AND deal_tag='0'