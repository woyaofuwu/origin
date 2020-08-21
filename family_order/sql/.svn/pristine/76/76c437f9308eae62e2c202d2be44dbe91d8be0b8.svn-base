UPDATE tf_f_account
   SET remove_tag='1',remove_date=to_date(:FINISH_DATE,'yyyy-mm-dd hh24:mi:ss'),update_time=to_date(:FINISH_DATE,'yyyy-mm-dd hh24:mi:ss')
 WHERE acct_id=TO_NUMBER(:ACCT_ID) AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)