select to_char(u.cust_id) cust_id, to_char(g.cust_name) cust_name, g.pspt_type_code, g.pspt_id, g.eparchy_code, g.city_code
  from tf_f_user u, tf_f_customer g, tf_f_cust_group p
 where 1 = 1
   and u.remove_tag = '0'
   and g.remove_tag = '0'
   and p.remove_tag = '0'
   and u.cust_id = g.cust_id
   and g.cust_id = p.cust_id
   and u.serial_number = :SERIAL_NUMBER