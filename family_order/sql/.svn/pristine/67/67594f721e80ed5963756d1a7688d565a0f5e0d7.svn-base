INSERT INTO tm_a_cardaccount(acct_id,bank_code,bank_acct_no,
 pay_mode_code,rsrv_tag1,rsrv_tag2,updatetime,eparchy_code)
 SELECT acct_id,bank_code,bank_acct_no,pay_mode_code,1 rsrv_tag1,
 0 rsrv_tag2,sysdate,eparchy_code from tf_f_account
 WHERE acct_id=to_number(:ACCT_ID)
 and partition_id=mod(to_number(:ACCT_ID),10000)