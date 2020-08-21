UPDATE tf_f_account
   SET remove_tag='0', remove_date=NULL, update_time=sysdate
 WHERE acct_id = TO_NUMBER(:ACCT_ID)
   AND partition_id = MOD(TO_NUMBER(:ACCT_ID),10000)
   AND remove_tag != '0'