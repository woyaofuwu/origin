delete from tf_f_user_acctday t 
       where partition_id=MOD(TO_NUMBER(:USER_ID),10000)
	   AND user_id=TO_NUMBER(:USER_ID)