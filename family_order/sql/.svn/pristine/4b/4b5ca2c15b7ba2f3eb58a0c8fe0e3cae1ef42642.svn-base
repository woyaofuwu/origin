INSERT INTO tf_f_user_res(partition_id,user_id,res_type_code,res_code,imsi,start_date,end_date,update_time,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
)
SELECT MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),res_type_code,res_code,imsi,start_date,end_date,update_time,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
  FROM tf_b_trade_res
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND (modify_tag = '0' OR  modify_tag = '3')