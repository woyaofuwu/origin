delete from tf_f_account_acctday t 
       where partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
	   AND acct_id=TO_NUMBER(:ACCT_ID)