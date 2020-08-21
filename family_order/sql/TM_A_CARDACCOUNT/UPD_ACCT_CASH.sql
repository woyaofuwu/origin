UPDATE tf_f_account SET pay_mode_code='0'
 WHERE bank_code=:BANK_CODE
 and pay_mode_code=:PAY_MODE_CODE 
 and eparchy_code=:EPARCHY_CODE