UPDATE tf_f_accountdeposit_rc
SET owe_fee = :OWE_FEE
WHERE partition_id=:PARTITION_ID
   AND acct_id=:ACCT_ID
   AND deposit_code=0