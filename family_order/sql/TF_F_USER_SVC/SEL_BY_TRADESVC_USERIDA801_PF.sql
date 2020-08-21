SELECT distinct to_char(ua.user_id) user_id,ua.partition_id,to_char(ua.user_id_a) user_id_a,ua.service_id,ua.main_tag,ua.inst_id,
ua.campn_id,to_char(ua.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(ua.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
to_char(ua.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,ua.update_staff_id,ua.update_depart_id,ua.remark,
ua.rsrv_num1,ua.rsrv_num2,ua.rsrv_num3,ua.rsrv_num4,ua.rsrv_num5,ua.rsrv_str1,ua.rsrv_str2,ua.rsrv_str3,ua.rsrv_str4,ua.rsrv_str5,
ua.rsrv_date1,ua.rsrv_date2,ua.rsrv_date3,ua.rsrv_tag1,ua.rsrv_tag2,ua.rsrv_tag3 
	 from ucr_crm1.tf_b_trade_svc s,TF_F_USER_SVC u ,TF_F_USER_SVC ua 
where   
   s.trade_id=:TRADE_ID   AND s.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
		 AND u.user_id=s.user_id    AND u.PARTITION_ID = MOD(s.USER_ID, 10000)  AND u.service_id='860'
         AND ua.user_id=u.user_id_a AND ua.PARTITION_ID = MOD(u.user_id_a, 10000)  AND ua.service_id='801' 
     AND sysdate BETWEEN u.start_date AND u.end_date 
