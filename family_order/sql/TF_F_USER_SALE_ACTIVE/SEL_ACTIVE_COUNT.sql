select count(1) count from TF_F_USER_SALE_ACTIVE a where 
 a.partition_id = mod(to_number(:USER_ID), 10000)
   and a.user_id = to_number(:USER_ID)
   and process_tag in( '0','4')
   and nvl(a.rsrv_date2,a.end_date) > sysdate
   and a.end_date>a.start_date
and product_id not in(SELECT para_code1 FROM  td_s_commpara WHERE param_attr = '158' and param_code='0'   and subsys_code = 'CSM'
    	and end_date > sysdate)
and package_id not in (SELECT para_code1 FROM  td_s_commpara WHERE param_attr = '158' and param_code='1'   and subsys_code = 'CSM'
    	and end_date > sysdate)