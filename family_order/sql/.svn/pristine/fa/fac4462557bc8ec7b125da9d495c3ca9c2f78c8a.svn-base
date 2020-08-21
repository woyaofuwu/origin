select count(*) recordcount from tf_f_accountdeposit a ,tf_a_payrelation b where
a.deposit_code=:DEPOSIT_CODE
and a.money>TO_NUMBER(:MONEY)
And b.user_id=:USER_ID
And a.acct_id=b.acct_id
And b.default_tag='1'