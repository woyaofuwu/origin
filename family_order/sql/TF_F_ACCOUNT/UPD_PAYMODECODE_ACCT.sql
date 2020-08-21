UPDATE tf_f_account
   SET pay_mode_code=:PAY_MODE_CODE,rsrv_str7=:RSRV_STR7,update_time=sysdate
 WHERE acct_id=TO_NUMBER(:ACCT_ID) 
AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)