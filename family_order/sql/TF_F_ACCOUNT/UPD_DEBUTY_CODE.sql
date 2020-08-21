UPDATE TF_F_ACCOUNT a 
 SET a.DEBUTY_CODE = :DEBUTY_CODE
 WHERE a.ACCT_ID = to_number(:ACCT_ID)
   AND a.partition_id = mod(to_number(:ACCT_ID),10000)
   AND a.REMOVE_TAG = '0'