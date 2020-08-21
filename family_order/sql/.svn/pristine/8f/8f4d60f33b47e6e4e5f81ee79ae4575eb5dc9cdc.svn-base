SELECT partition_id,to_char(user_id) user_id,cust_name,sex,to_char(birthday,'yyyy-mm-dd hh24:mi:ss') birthday,school_name,grad_school,classname,main_duty,interest,serial_number1,serial_number2,active_flag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,rsrv_str1,rsrv_str2,to_char(rsrv_num3) rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_dat5,'yyyy-mm-dd hh24:mi:ss') rsrv_dat5,to_char(rsrv_dat6,'yyyy-mm-dd hh24:mi:ss') rsrv_dat6 
  FROM tf_f_user_monitor
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND end_date> sysdate