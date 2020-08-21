SELECT to_char(log_id) log_id,start_res_no,end_res_no,res_type_code,eparchy_code,res_kind_code,capacity_type_code,assign_tag,to_char(assign_time,'yyyy-mm-dd hh24:mi:ss') assign_time,assign_staff_id,to_char(assign_num) assign_num,eparchy_code_o,eparchy_code_n,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_cen_resassign_log
 WHERE log_id=TO_NUMBER(:LOG_ID)
   AND start_res_no=:START_RES_NO
   AND end_res_no=:END_RES_NO
   AND res_type_code=:RES_TYPE_CODE