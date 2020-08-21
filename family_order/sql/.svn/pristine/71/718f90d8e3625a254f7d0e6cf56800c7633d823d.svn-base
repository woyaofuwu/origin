select count(1) count
  from TF_F_USER_SALE_ACTIVE a, td_s_commpara b
 where a.partition_id = mod(to_number(:USER_ID), 10000)
   and a.user_id = to_number(:USER_ID)
   and process_tag in( '0','4')
   and nvl(a.rsrv_date2,a.end_date) > sysdate
   and a.end_date>a.start_date
   and (a.product_id = b.para_code1 or a.package_id = b.para_code1)
   and b.param_attr = '155'
   and b.subsys_code = 'CSM'
   and (b.eparchy_code = :EPARCHY_CODE or b.eparchy_code = 'ZZZZ')
   and b.end_date>sysdate