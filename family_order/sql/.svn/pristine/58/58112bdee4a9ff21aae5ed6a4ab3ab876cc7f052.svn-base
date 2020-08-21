insert into TF_B_TRADE_OTHER
						  (TRADE_ID,
						  ACCEPT_MONTH    ,
						  USER_ID         ,
						  RSRV_VALUE_CODE ,
						  RSRV_VALUE      ,  
						  RSRV_NUM1,
						  RSRV_NUM2,
						  RSRV_NUM3,
						  RSRV_STR9 ,
						  RSRV_STR11,
						  START_DATE, 
						  END_DATE,
						  MODIFY_TAG)
						  select to_number(:TRADE_ID),
						         :ACCEPT_MONTH,
						         u.user_id_b,
						         'CNTRX',
						         'CNTRX成员',
						         '0',
						         '0',
						         '0',
						         '6301',
						         'D',
						         sysdate -1,
						         sysdate,
						         '0'
						    from tf_f_relation_uu u 
						      where 1=1 
						      and u.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
						      AND u.user_id_b=TO_NUMBER(:USER_ID)
						      and u.relation_type_code in ('S3')
						      AND SYSDATE between u.start_date and u.end_date
      						AND u.end_date>last_day(trunc(sysdate))+1-1/24/3600
						      and rownum < 2