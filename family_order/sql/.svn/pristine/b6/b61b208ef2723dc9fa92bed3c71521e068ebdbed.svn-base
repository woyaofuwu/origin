select partition_id,to_char(user_id) user_id,rsrv_value_code,inst_id,
    rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,
    rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,
    to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
    to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
 from  tf_f_user_other
 where 1=1
   and user_id=(select to_number(user_id) from tf_f_user where serial_number = substr(:SERIAL_NUMBER, 4) and remove_tag='0')
   and rsrv_value_code='ZNZW_ACCT_IN'
   and sysdate between start_date and end_date