SELECT a.serial_number,a.user_id,a.product_id,a.package_id,a.product_name,a.package_name,a.process_tag,a.campn_type
FROM tf_f_user_sale_active a WHERE a.user_id = to_number(:USER_ID)
      AND a.partition_id = mod(to_number(:USER_ID),10000)
      AND a.end_date >  to_date(:LIMIT_CODE,'yyyy-mm-dd hh24:mi:ss')
      and a.end_date >= SYSDATE
      AND a.end_date > a.start_date
      and a.product_id NOT in(SELECT para_code1 FROM  td_s_commpara WHERE param_attr = '155' and param_code='0' and subsys_code = 'CSM'
      and end_date > sysdate)
