update tf_f_accountdeposit
	set draw_money = draw_money + to_number(:MONEY)
  where acct_id=to_number(:ACCT_ID)
  and partition_id=mod(to_number(:ACCT_ID),10000)
  and deposit_code=:DEPOSIT_CODE