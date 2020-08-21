SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,to_char(chnl_user_id) chnl_user_id,join_cause,serial_number1,serial_number2,serial_number3,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,rsrv_str1,rsrv_str2,to_char(rsrv_num3) rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_dat5,'yyyy-mm-dd hh24:mi:ss') rsrv_dat5,to_char(rsrv_dat6,'yyyy-mm-dd hh24:mi:ss') rsrv_dat6 
  FROM tf_f_user_pclass
 WHERE chnl_user_id =:CHNL_USER_ID
  AND end_date > sysdate