select nvl(to_char(max(end_date), 'yyyy-mm-dd hh24:mi:ss'), '1990-12-12 21:21:21') end_date
  from TF_F_USER_SALE_ACTIVE
 where partition_id = mod(to_number(:USER_ID), 10000)
 and user_id = to_number(:USER_ID)
 and process_tag in( '0','4')
    and end_date > sysdate
   and end_date > start_date
and product_id not in(SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='0'   and subsys_code = 'CSM'
      and end_date > sysdate)
    and package_id not in (SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='1'   and subsys_code = 'CSM'
      and end_date > sysdate)