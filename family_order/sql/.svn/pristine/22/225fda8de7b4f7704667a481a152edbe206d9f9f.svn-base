update tf_f_user_other t
	 set t.rsrv_str1=:NEW_RESNO,t.rsrv_str14=:OLD_RESNO,t.rsrv_str15=:CALL_PARAMS,t.rsrv_str16=to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')
	 where t.rsrv_value_code in ('FTTH','FTTH_GROUP')
	 and t.user_id=to_number(:USER_ID)
	 and t.PARTITION_ID=to_number(MOD(:USER_ID,10000))
	 and T.RSRV_STR1=:OLD_RESNO
	 AND SYSDATE < T.END_DATE