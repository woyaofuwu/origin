SELECT /*+ ordered use_nl(s,a)*/nvl(to_char(max(end_date), 'yyyy-mm-dd hh24:mi:ss'),
           to_char(sysdate, 'yyyy-mm-dd hh24:mi:ss')) BOOK_DATE
FROM (SELECT * FROM (SELECT * FROM tf_f_user_sale_active a WHERE a.user_id = to_number(:USER_ID)
      AND a.partition_id = mod(to_number(:USER_ID),10000)
      and a.end_date > sysdate
      and process_tag in( '0','4')
     and product_id not in(SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='0'   and subsys_code = 'CSM'
      and end_date > sysdate)
    and package_id not in (SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='1'   and subsys_code = 'CSM'
      and end_date > sysdate)
      ORDER BY a.end_date DESC) WHERE ROWNUM = 1) s
WHERE s.user_id = to_number(:USER_ID)
  AND s.partition_id = mod(to_number(:USER_ID),10000)
  AND nvl(s.rsrv_date2,s.end_date) > SYSDATE