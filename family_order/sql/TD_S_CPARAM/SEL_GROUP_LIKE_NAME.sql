SELECT count(*) recordcount
 FROM tf_f_cust_group g
WHERE cust_name LIKE '%'||:PARAM0||'%'
and g.remove_tag = '0'