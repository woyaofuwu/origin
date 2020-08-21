select B.USER_ID
  from TF_F_USER_PRODUCT B,TF_F_USER_OTHER A
 where  A.USER_ID=B.USER_ID
   AND B.product_id = '10001001'
   AND B.brand_code = 'CPE1'
   and sysdate between B.start_date and B.end_date
   and B.start_date > trunc(SYSDATE, 'dd') - to_number(:DAYNUM)
   and B.start_date < trunc(SYSDATE, 'dd') - (to_number(:DAYNUM)-1)
	 AND A.rsrv_value_code = 'CPE_DEVICE' 
	 AND A.RSRV_VALUE=:RSRV_VALUE
	 AND A.RSRV_STR2 IS NULL
	 AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
union   
select a.user_id
  from TF_F_USER_OTHER A
  where  A.rsrv_value_code = 'CPE_DEVICE'
   and A.rsrv_value = :RSRV_VALUE
   and sysdate between A.start_date and A.end_date
   and A.start_date > trunc(SYSDATE, 'dd') - to_number(:DAYNUM)
   and A.start_date < trunc(SYSDATE, 'dd') - (to_number(:DAYNUM)-1)