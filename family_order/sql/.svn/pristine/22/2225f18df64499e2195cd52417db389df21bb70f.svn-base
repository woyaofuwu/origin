SELECT partition_id,to_char(user_id) user_id,res_type_code,res_code,res_info1,res_info2,res_info3,res_info4,res_info5,res_info6,res_info7,res_info8,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_res
 WHERE res_type_code=:RES_TYPE_CODE
   AND res_code=:RES_CODE
   AND res_info8=:USER_ID_A
   AND end_date > sysdate