SELECT partition_id,to_char(user_id) user_id,brand_code,brand_no,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_num1,trade_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_staff_id,update_depart_id 
  FROM tf_f_user_brandchange
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND sysdate BETWEEN start_date AND end_date