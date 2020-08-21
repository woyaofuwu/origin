select produce_sub_id,to_char(produce_batch_id) produce_batch_id,res_type_code,start_res_no,end_res_no, 
       res_kind_code,capacity_type_code,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,staff_id_in, 
       to_char(assign_num) assign_num,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date, 
       to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date, data_file, check_file,
       to_char(assign_time,'yyyy-mm-dd hh24:mi:ss') assign_time,assign_staff_id,receive_tag, 
       to_char(receive_date,'yyyy-mm-dd hh24:mi:ss') receive_date,finish_tag, 
       to_char(finish_produce_date,'yyyy-mm-dd hh24:mi:ss') finish_produce_date,back_tag,apply_no, 
       to_char(apply_batch_id) apply_batch_id,apply_detail_no,produce_id,to_char(batch_id) batch_id, 
       audit_no,audit_staff_id,to_char(audit_date,'yyyy-mm-dd hh24:mi:ss') audit_date, audit_remark,
       audit_type_code,remind_code_a,to_char(remind_date_a,'yyyy-mm-dd hh24:mi:ss') remind_date_a,remind_code_b,to_char(remind_date_b,'yyyy-mm-dd hh24:mi:ss') remind_date_b,
       remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
       rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3
FROM tf_b_res_produce_sub
where (time_in>=TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss') and time_in<=TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')) 
and (:RECEIVE_TAG is null or receive_tag = :RECEIVE_TAG)
and (:FINISH_TAG is null or finish_tag = :FINISH_TAG )
and (:PRODUCE_SUB_ID is null or produce_sub_id = :PRODUCE_SUB_ID)
and (:APPLY_NO is null or apply_no = :APPLY_NO)
and (:RES_TYPE_CODE is null or res_type_code = :RES_TYPE_CODE)
and (:RES_KIND_CODE is null or res_kind_code = :RES_KIND_CODE)
and audit_type_code='6'--已经下达