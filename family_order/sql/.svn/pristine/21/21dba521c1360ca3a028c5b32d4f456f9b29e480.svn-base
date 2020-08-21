SELECT to_char(user_id) user_id,
       serial_number,
       bind_serial_number,
       rsrv_value_code,
       rsrv_value,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       rsrv_str7,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_bind
 WHERE user_id = to_number(:USER_ID)