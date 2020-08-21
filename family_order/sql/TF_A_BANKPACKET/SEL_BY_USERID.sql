SELECT partition_id,to_char(user_id) user_id,serial_number,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,make_state,make_terminal,commender_id,rsrv_str1,rsrv_str2,rsrv_str3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,rsrv_num1,to_char(rsrv_num2) rsrv_num2,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark 
  FROM tf_a_bankpacket a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.make_state=:MAKE_STATE
   AND sysdate between a.start_date and a.end_date