SELECT a.serial_number,a.user_id,a.product_id,a.package_id,a.product_name,a.package_name,a.process_tag 
FROM tf_f_user_sale_active a WHERE a.user_id = to_number(:USER_ID)
      AND a.partition_id = mod(to_number(:USER_ID),10000)
      and nvl(a.rsrv_date2,a.end_date) > sysdate
      and a.process_tag in( '0','4')
      and a.product_id not in(SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='0' and subsys_code = 'CSM'
      and end_date > sysdate)
      and a.package_id not in (SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='1' and subsys_code = 'CSM'
      and end_date > SYSDATE)