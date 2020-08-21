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
					  RSRV_STR12,
					  START_DATE, 
					  END_DATE,
					  MODIFY_TAG)
					  select to_number(:TRADE_ID),
					         :ACCEPT_MONTH,
					         v.user_id,
					         'CNTRX',
					         'CNTRX成员',
					         '0',
					         '0',
					         '0',
					         '8171',
					         :RSRV_STR11,
					         :RSRV_STR12,
					         sysdate -1,
					         sysdate,
					         '0'
					    from tf_f_user_impu v
					   where v.partition_id = MOD(to_number(:USER_ID), 10000)
					     and v.user_id = to_number(:USER_ID)
                                             and v.RSRV_STR1 = '0'
					     and sysdate between v.start_date and v.end_date