SELECT to_char(user_id) user_id,partition_id,serial_number,data_type,cust_type,state_code,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
update_time,update_staff_id,update_depart_id,rsrv_str1,rsrv_str2,rsrv_str3,
DECODE(data_type,'0','垃圾短信','1','骚扰电话','未知')||'-'||DECODE(cust_type,'0','白名单','1','黑名单','未知') field_name1,
DECODE(state_code,'0','激活','1','暂停','未知') field_name2,
'' field_name3,
'' field_name4,
'' field_name5 
  FROM tf_f_user_trash
 WHERE serial_number = :SERIAL_NUMBER
   AND data_type = :DATA_TYPE
   AND cust_type = :CUST_TYPE
ORDER BY data_type,cust_type,start_date