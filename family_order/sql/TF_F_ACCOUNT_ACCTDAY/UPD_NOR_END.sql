update tf_f_account_acctday t 
       set end_date = to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),       
		   UPDATE_TIME         = SYSDATE,
           UPDATE_STAFF_ID     = :UPDATE_STAFF_ID,
           UPDATE_DEPART_ID    = :UPDATE_DEPART_ID
	   where acct_id = :ACCT_ID
	   and   end_date>sysdate