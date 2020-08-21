SELECT g.group_id,g.cust_name,t.user_id,t.serial_number,v.vpn_no,g.city_code  
  FROM tf_f_user_vpn v,tf_f_cust_group g,tf_f_user t 
    WHERE t.cust_id = g.cust_id AND t.user_id = v.user_id AND t.partition_id = v.partition_id 
    AND v.vpn_name LIKE '%'||:VPN_NAME||'%' 
    AND g.group_id = :GROUP_ID 
    AND v.vpn_no = :VPN_NO 
    AND g.city_code = :CITY_CODE 
    AND t.remove_tag = '0' AND g.remove_tag = '0' AND v.remove_tag = '0' 