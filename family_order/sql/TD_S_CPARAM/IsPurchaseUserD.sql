SELECT a.user_id, to_char(a.end_date, 'YYYY-MM-DD HH24:MI:SS') end_date, a.campn_name, a.campn_code 
FROM   tf_f_user_sale_active a
WHERE  user_id = :USER_ID
And    (product_id = :PRODUCT_ID OR :PRODUCT_ID = '99')
AND    process_tag in ('0','2')
AND    rsrv_date2 > trunc(sysdate, 'mm')
AND partition_id = mod(to_number(:USER_ID),10000)
AND a.package_id not in (SELECT to_number(t.para_code1) FROM  td_s_commpara t where t.subsys_code='CSM' and t.param_attr=25 and t.param_code='NPPKG')