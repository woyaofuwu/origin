select T.* from tl_b_stopuser t,tf_f_user t1
	 where t.serial_number=t1.serial_number
	 and t.deal_status='1'
	 AND T.DEAL_TIME IS NOT NULL 
	 and t1.remove_tag='0'
	 and t1.user_id=:USER_ID