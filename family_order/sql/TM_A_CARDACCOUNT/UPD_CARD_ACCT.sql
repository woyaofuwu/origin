UPDATE tf_f_account SET pay_mode_code=:PAY_MODE_CODE, 
 bank_code=:BANK_CODE,bank_acct_no=:BANK_ACCT_NO
 WHERE acct_id=to_number(:ACCT_ID)
 and partition_id=mod(to_number(:ACCT_ID),10000)