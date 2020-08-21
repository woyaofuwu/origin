SELECT partition_id,to_char(user_id) user_id,serial_number,to_char(regist_date,'yyyy-mm-dd hh24:mi:ss') regist_date,rsrv_str1,rsrv_str2,to_char(rsrv_num3) rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_dat5,'yyyy-mm-dd hh24:mi:ss') rsrv_dat5,to_char(rsrv_dat6,'yyyy-mm-dd hh24:mi:ss') rsrv_dat6 
  FROM tf_f_user_ingw_inc
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)