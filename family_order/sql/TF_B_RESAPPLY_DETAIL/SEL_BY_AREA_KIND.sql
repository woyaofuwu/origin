SELECT a.apply_detail_no,a.res_type_code,a.start_res_no,a.end_res_no,a.res_kind_code,a.capacity_type_code,to_char(a.apply_num) apply_num,a.factory_code,a.brand_code,a.audit_no,a.audit_staff_id,a.audit_depart_id,to_char(a.audit_date,'yyyy-mm-dd hh24:mi:ss') audit_date,a.audit_remark,a.audit_type_code,a.remind_code_a,to_char(a.remind_date_a,'yyyy-mm-dd hh24:mi:ss') remind_date_a,a.remind_code_b,to_char(a.remind_date_b,'yyyy-mm-dd hh24:mi:ss') remind_date_b,a.apply_no,to_char(a.apply_batch_id) apply_batch_id,a.remark,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,b.apply_area_code rsrv_str1,a.rsrv_str2,a.rsrv_str3,
a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7
,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,0 x_tag 
  FROM tf_b_resapply_detail a,tf_b_resapply_main b
where a.apply_no = b.apply_no
and a.apply_batch_id = b.apply_batch_id
and b.apply_date >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
and b.apply_date <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
and b.apply_type_code = :APPLY_TYPE_CODE
and b.apply_area_code = :APPLY_AREA_CODE
and b.res_type_code=:RES_TYPE_CODE
and (:RES_KIND_CODE is null or a.res_kind_code=:RES_KIND_CODE)
and (:CAPACITY_TYPE_CODE is null or a.capacity_type_code=:CAPACITY_TYPE_CODE)
and (:RSRV_STR5 is null or a.rsrv_str5=:RSRV_STR5)
and (:RSRV_STR4 is null or a.rsrv_str4=:RSRV_STR4)
and (:APPLY_STATUS is null or b.apply_status=:APPLY_STATUS)
and b.audit_type_code !='0' 
and b.audit_state_code ='1'
and (:AUDIT_TYPE_CODE is null or a.audit_type_code =:AUDIT_TYPE_CODE)