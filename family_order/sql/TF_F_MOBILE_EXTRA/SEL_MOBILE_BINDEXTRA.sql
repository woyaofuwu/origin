SELECT partition_id,to_char(user_id) user_id,serial_number,bind_serial_number,obj_cust_name,user_type_code,pspt_type_code,pspt_id,work_address,payfor_way_code,bindsale_attr,discnt_code,purchase_attr,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_tag1,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,oper_staff_id,to_char(oper_time,'yyyy-mm-dd hh24:mi:ss') oper_time 
  FROM tf_f_mobile_extra
 WHERE bind_serial_number=:BIND_SERIAL_NUMBER
  AND sysdate between start_date and end_date