SELECT distinct to_char(s.user_id) user_id,r.partition_id,to_char(r.USER_ID_A) USER_ID_A,r.res_type_code,r.res_code,r.imsi,r.KI,to_char(r.INST_ID) INST_ID,to_char(r.CAMPN_ID) CAMPN_ID,to_char(r.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(r.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(r.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,r.update_staff_id,r.update_depart_id,r.remark,r.rsrv_num1,r.rsrv_num2,r.rsrv_num3,to_char(r.rsrv_num4) rsrv_num4,to_char(r.rsrv_num5) rsrv_num5,r.rsrv_str1,r.rsrv_str2,r.rsrv_str3,r.rsrv_str4,r.rsrv_str5,to_char(r.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(r.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(r.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,r.rsrv_tag1,r.rsrv_tag2,r.rsrv_tag3 
  FROM  ucr_crm1.tf_b_trade t, ucr_crm1.tf_b_trade_svc s ,tf_f_user_res r 
 WHERE   s.user_id=r.user_id  AND r.PARTITION_ID = MOD(s.USER_ID, 10000)
	 AND t.trade_id=:TRADE_ID AND t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
	 AND s.trade_id=:TRADE_ID AND s.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
	 AND r.end_date >= SYSDATE
	 AND s.user_id<>t.user_id