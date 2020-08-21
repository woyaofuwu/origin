SELECT request_id ,accept_month ,to_char(cust_id) cust_id ,
		to_char(cust_name) cust_name ,to_char(user_id) user_id ,
		serial_number ,request_type_code ,request_sub_type ,
		request_pri ,serv_item ,
		to_char(dead_line,'yyyy-mm-dd hh24:mi:ss') dead_line ,
		request_state ,
		to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date ,
		to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date ,
		request_result ,rsrv_str1 ,rsrv_str2 ,rsrv_str3 ,
		rsrv_str4 ,rsrv_str5 ,rsrv_str6 ,rsrv_str7 ,
		rsrv_str8 ,rsrv_str9 ,rsrv_str10,remark
  FROM  tf_b_serv_request_info
 WHERE request_id=:REQUEST_ID
   AND accept_month=:ACCEPT_MONTH