select a.USER_ID
   from TF_F_USER_PRODUCT A, tf_f_active_template b,TF_F_USER_OTHER c
   where a.user_id=b.user_id
	 and a.user_id=c.user_id
   and A.product_id = '10001001'
   AND A.brand_code = 'CPE1' 
	 and b.trade_type_code='697'
   and sysdate between A.start_date and A.end_date
   and b.deal_tag='0'
   and to_char(sysdate,'yyyymmdd')=b.rsrv_str2
	 and c.rsrv_value_code = 'CPE_LOCATION' 
   AND C.RSRV_VALUE='1'
	 and sysdate between c.start_date and c.end_date