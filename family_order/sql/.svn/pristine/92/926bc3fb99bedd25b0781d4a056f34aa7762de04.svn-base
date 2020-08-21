update tf_f_user_other t
	 set t.rsrv_str4=:NEW_RESNO,t.rsrv_str14=:OLD_RESNO,t.rsrv_str15=:CALL_PARAMS,t.rsrv_str16=to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')
	 where (t.rsrv_value_code ='CPE_DEVICE' or t.rsrv_value_code ='CPE_LOCATION')
	 and t.user_id=:USER_ID
	 and t.PARTITION_ID=MOD(:USER_ID,10000)
	 and T.RSRV_STR4=:OLD_RESNO
	 AND SYSDATE < T.END_DATE