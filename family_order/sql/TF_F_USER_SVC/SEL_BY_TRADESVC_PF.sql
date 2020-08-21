SELECT distinct to_char(s.user_id) user_id,u.partition_id,to_char(u.user_id_a) user_id_a,u.service_id,u.main_tag,u.inst_id,
u.campn_id,to_char(u.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(u.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
to_char(u.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,u.update_staff_id,u.update_depart_id,u.remark,
u.rsrv_num1,u.rsrv_num2,u.rsrv_num3,u.rsrv_num4,u.rsrv_num5,u.rsrv_str1,u.rsrv_str2,u.rsrv_str3,u.rsrv_str4,u.rsrv_str5,
u.rsrv_date1,u.rsrv_date2,u.rsrv_date3,u.rsrv_tag1,u.rsrv_tag2,u.rsrv_tag3 
  FROM  ucr_crm1.tf_b_trade t, ucr_crm1.tf_b_trade_svc s ,tf_f_user_svc u 
 WHERE   s.user_id=u.user_id  AND u.PARTITION_ID = MOD(s.USER_ID, 10000)
		 AND t.trade_id=:TRADE_ID  AND t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) 
		 AND s.trade_id=:TRADE_ID AND s.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
		 AND sysdate BETWEEN u.start_date AND u.end_date
		 AND s.user_id<>t.user_id
