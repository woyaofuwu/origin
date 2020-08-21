SELECT a.apply_detail_no,a.res_type_code,a.start_res_no,a.end_res_no,a.res_kind_code,
a.capacity_type_code,to_char(a.apply_num) apply_num,a.factory_code,a.brand_code,
a.audit_no,a.audit_staff_id,a.audit_depart_id,to_char(a.audit_date,'yyyy-mm-dd hh24:mi:ss') audit_date,
a.audit_remark,a.audit_type_code,a.remind_code_a,to_char(a.remind_date_a,'yyyy-mm-dd hh24:mi:ss') remind_date_a,
a.remind_code_b,to_char(a.remind_date_b,'yyyy-mm-dd hh24:mi:ss') remind_date_b,a.apply_no,
to_char(a.apply_batch_id) apply_batch_id,b.remark remark,b.apply_status rsrv_tag1,b.close_type_code rsrv_tag2,b.audit_state_code rsrv_tag3,
to_char(b.time_in,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(b.apply_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
to_char(b.close_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
a.rsrv_str1,b.apply_area_code rsrv_str2,b.apply_staff_id rsrv_str3,b.apply_reason rsrv_str4,b.cancel_reason rsrv_str5,
b.close_staff_id rsrv_str6,a.rsrv_str7,b.priority rsrv_num1,a.rsrv_num2,a.rsrv_num3 
from tf_b_resapply_detail a,tf_b_resapply_main b
where a.apply_no = b.apply_no
and a.apply_batch_id = b.apply_batch_id
and b.apply_date >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
and b.apply_date <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
and (:APPLY_TYPE_CODE is null or b.apply_type_code = :APPLY_TYPE_CODE)
and (:APPLY_NO is null or b.apply_no = :APPLY_NO)
and (:APPLY_BATCH_ID is null or b.apply_batch_id = :APPLY_BATCH_ID)
and (:APPLY_STAFF_ID is null or b.apply_staff_id = :APPLY_STAFF_ID)
and (:APPLY_AREA_CODE is null or b.apply_area_code = :APPLY_AREA_CODE)
and (:APPLY_STATUS is null or b.apply_status =:APPLY_STATUS )
and (:AUDIT_STATE_CODE is null or b.audit_state_code = :AUDIT_STATE_CODE)