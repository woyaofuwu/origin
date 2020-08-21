SELECT to_char(log_id) log_id,start_res_no,end_res_no,eparchy_code,num_type_code,capacity_type_code,assign_tag,to_char(assign_time,'yyyy-mm-dd hh24:mi:ss') assign_time,assign_staff_id,to_char(assign_num) assign_num,eparchy_code_o,city_code_o,depart_id_o,staff_id_o,eparchy_code_n,city_code_n,depart_id_n,staff_id_n,sub_log_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_kiassign_log
 WHERE (:ASSIGN_TAG is null or assign_tag=:ASSIGN_TAG)
   AND assign_time=TO_DATE(:ASSIGN_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND (:ASSIGN_STAFF_ID is null or assign_staff_id=:ASSIGN_STAFF_ID)
   AND (:EPARCHY_CODE_N is null or eparchy_code_n=:EPARCHY_CODE_N)