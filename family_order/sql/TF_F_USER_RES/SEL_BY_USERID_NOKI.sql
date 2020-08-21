SELECT partition_id,to_char(user_id) user_id,res_type_code,res_code,res_info1,'' res_info2,res_info3,res_info4,res_info5,res_info6,res_info7,res_info8,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_res
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date >= SYSDATE