UPDATE tf_f_accountdeposit_trans
   SET   
      DEAL_TIME=SYSDATE,
      DEAL_TAG='1'
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND deal_tag='0'