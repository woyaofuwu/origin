select t.*  from  tf_f_user_other t where t.rsrv_value_code in ('FTTH','FTTH_GROUP')
	 and t.user_id=TO_NUMBER(:USER_ID)
	 and t.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
	 and T.RSRV_STR1=:OLD_RESNO
	 AND SYSDATE < T.END_DATE