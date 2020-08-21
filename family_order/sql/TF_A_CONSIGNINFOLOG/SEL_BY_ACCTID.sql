SELECT to_char(acct_id) acct_id,acyc_id,commit_tag
  FROM tf_a_consigninfolog
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND acyc_id=:ACYC_ID
   AND recv_acyc_id=(select acyc_id from td_a_acycpara where 
bcyc_id=to_char(sysdate ,'yyyymm'))