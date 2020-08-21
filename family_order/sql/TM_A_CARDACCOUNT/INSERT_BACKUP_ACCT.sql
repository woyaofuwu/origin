INSERT INTO tm_a_cardaccount(acct_id,bank_code,bank_acct_no,
 pay_mode_code,rsrv_tag1,rsrv_tag2,updatetime,eparchy_code)
 SELECT acct_id,bank_code,bank_acct_no,pay_mode_code,0 rsrv_tag1,
 0 rsrv_tag2,sysdate,eparchy_code from tf_f_account
 WHERE bank_code =:BANK_CODE
 and pay_mode_code=:PAY_MODE_CODE
 and eparchy_code=:EPARCHY_CODE