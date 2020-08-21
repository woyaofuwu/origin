UPDATE tf_f_account
   SET remove_tag='1',remove_date=SYSDATE,update_time=sysdate 
 WHERE acct_id=TO_NUMBER(:ACCT_ID) AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)