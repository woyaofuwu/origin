select  u.serial_number, g.cust_name, u.brand_code, u.score_value, u.eparchy_code, to_char(u.open_date,'yyyy-mm-dd hh24:mi:ss') open_date
  from  tf_f_cust_group g, tf_f_user u
where   1 = 1
  and   g.group_id = :GROUP_ID
  and   g.cust_id   = u.cust_id
  and   u.partition_id = mod(u.user_id,10000)
  and   u.remove_tag = '0'