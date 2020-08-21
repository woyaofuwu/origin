DELETE FROM ts_a_subgroupbill
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=mod(TO_NUMBER(:ACCT_ID),10000)
   AND acyc_id=:ACYC_ID