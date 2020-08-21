select t.* from tf_f_user_other t
	 where t.rsrv_value_code ='CPE_DEVICE' 
	 and t.user_id=:USER_ID
	 and t.PARTITION_ID=MOD(:USER_ID,10000)
	 and T.RSRV_STR4=:OLD_RESNO
	 AND SYSDATE < T.END_DATE