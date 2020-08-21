SELECT eparchy_code,msg_no,res_type_code,start_res_no,end_res_no,res_kind_code,capacity_type_code,to_char(oper_num) oper_num,create_staff_id,to_char(create_time,'yyyy-mm-dd hh24:mi:ss') create_time,to_char(assign_time,'yyyy-mm-dd hh24:mi:ss') assign_time,assign_staff_id,assign_declare,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,staff_id_in,declare_in,audit_state_code,storage_info,storage_code_out,storage_code_in,cancel_reason,remind_code_a,to_char(remind_date_a,'yyyy-mm-dd hh24:mi:ss') remind_date_a,to_char(log_id) log_id,remind_code_b,to_char(remind_date_b,'yyyy-mm-dd hh24:mi:ss') remind_date_b,to_char(sub_log_id) sub_log_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag 
  FROM tf_b_cenassign_msg
 WHERE (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
   AND (:MSG_NO is null or msg_no=:MSG_NO)
   AND (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND create_time>=TO_DATE(:CREATE_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND create_time<=TO_DATE(:CREATE_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:AUDIT_STATE_CODE is null or audit_state_code=:AUDIT_STATE_CODE)