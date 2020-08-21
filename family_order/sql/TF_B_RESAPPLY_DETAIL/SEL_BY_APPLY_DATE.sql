SELECT     b.apply_detail_no,b.res_type_code,b.start_res_no,b.end_res_no,b.res_kind_code,b.capacity_type_code,to_char(b.apply_num) apply_num,b.factory_code,b.brand_code,b.audit_no,b.audit_staff_id,b.audit_depart_id,to_char(b.audit_date,'yyyy-mm-dd hh24:mi:ss') audit_date,b.audit_remark,b.audit_type_code,b.remind_code_a,to_char(b.remind_date_a,'yyyy-mm-dd hh24:mi:ss') remind_date_a,b.remind_code_b,to_char(b.remind_date_b,'yyyy-mm-dd hh24:mi:ss') remind_date_b,b.apply_no,to_char(b.apply_batch_id) apply_batch_id,b.remark,a.audit_type_code rsrv_tag1,a.audit_state_code rsrv_tag2,a.apply_status rsrv_tag3,to_char(a.time_in,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.apply_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.close_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,a.apply_staff_id rsrv_str1,a.apply_area_code rsrv_str2,a.apply_reason rsrv_str3, a.cancel_reason rsrv_str4,b.rsrv_str5,b.rsrv_str6,b.rsrv_str7,a.priority rsrv_num1,b.rsrv_num2,b.rsrv_num3 
  FROM tf_b_resapply_main a,tf_b_resapply_detail b
 WHERE a.apply_no = b.apply_no
   AND a.apply_batch_id = b.apply_batch_id
   AND a.apply_date >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND a.apply_date <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND (:APPLY_TYPE_CODE is null or a.apply_type_code = :APPLY_TYPE_CODE)
   AND (:APPLY_NO is null or a.apply_no = :APPLY_NO)
   AND (:APPLY_BATCH_ID is null or a.apply_batch_id = :APPLY_BATCH_ID)
   AND (:APPLY_STAFF_ID is null or a.apply_staff_id = :APPLY_STAFF_ID)
   AND (:APPLY_AREA_CODE is null or a.apply_area_code = :APPLY_AREA_CODE)
   AND (:APPLY_STATUS is null or a.apply_status =:APPLY_STATUS )
   AND (:AUDIT_STATE_CODE is null or a.audit_state_code = :AUDIT_STATE_CODE)  
   AND (:AUDIT_TYPE_CODE is null or a.audit_type_code = :AUDIT_TYPE_CODE)