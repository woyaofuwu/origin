select count(*) recordcount from tf_f_accountdeposit a where a.acct_id=:ACCT_ID and a.deposit_code=:DEPOSIT_CODE
and a.money<TO_NUMBER(:MONEY)