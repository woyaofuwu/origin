select  /*+ use_nl(t t1)*/ t.rsrv_str1,t.rsrv_str2,t1.user_id,t1.cust_id,t1.serial_number,t1.eparchy_code,t1.city_code,T.STAFF_ID,T.DEPART_ID,T.TRADE_ID,T.INST_ID,t.rsrv_str9
   from TF_F_USER_OTHER t,tf_f_user t1
  where t.user_id=t1.user_id
	  and t.partition_id=t1.partition_id 
    and sysdate between t.start_date and t.end_date
    and sysdate >  TRUNC(t.rsrv_date1 + 90 ) - 1 / 86400
    and to_number(t.rsrv_str2)>0
    and T.rsrv_str7='0'
    and t.rsrv_tag2='1'
    and t.rsrv_tag1='0'
    and t.rsrv_str9 in ('1','2')
		and t.rsrv_value_code = 'FTTH'