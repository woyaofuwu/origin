select g.city_code  from tf_f_user_vpn t, tf_f_user u, tf_f_cust_group g  
  where g.cust_id = u.cust_id and u.user_id = t.user_id and u.partition_id = t.partition_id 
    and u.remove_tag = '0' and g.remove_tag = '0' and t.remove_tag = '0' 
    and t.vpn_no = :VPN_NO 