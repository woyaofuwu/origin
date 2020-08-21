UPDATE tf_f_account a SET 
 (pay_mode_code,bank_code,bank_acct_no)=
 (select pay_mode_code,bank_code,bank_acct_no
 from tm_a_cardaccount where a.acct_id=acct_id and
 a.partition_id=mod(acct_id,10000) 
 and rsrv_tag1=:RSRV_TAG1 
 and eparchy_code=:EPARCHY_CODE )
 where (a.acct_id,a.partition_id)
 in (select acct_id,mod(acct_id,10000) from 
 tm_a_cardaccount where rsrv_tag1=:RSRV_TAG1)
 and eparchy_code=:EPARCHY_CODE