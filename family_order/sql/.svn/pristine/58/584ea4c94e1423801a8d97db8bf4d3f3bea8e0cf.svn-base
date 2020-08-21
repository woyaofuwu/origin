update tf_f_accountdeposit
	set deposit_money = deposit_money + to_number(:MONEY),
      inprint_fee = inprint_fee + to_number(:MONEY)
  where acct_id=to_number(:ACCT_ID)
  and partition_id=mod(to_number(:ACCT_ID),10000)
  and deposit_code=:DEPOSIT_CODE