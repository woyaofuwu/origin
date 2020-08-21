SELECT a.partition_id,to_char(a.user_id) user_id,a.service_id,a.para_code,b.para_name,a.para_type_code,a.ip_address,decode(a.rsrv_str1,'0','动态','1','静态',a.rsrv_str1) rsrv_str1,a.rsrv_str2,a.rsrv_str3,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_apn a, td_b_apnservice b
 WHERE a.para_code=b.para_code(+)
   AND partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)