SELECT apply_detail_no,res_type_code,start_res_no,end_res_no,res_kind_code,capacity_type_code,to_char(apply_num) apply_num,factory_code,brand_code,audit_no,audit_staff_id,audit_depart_id,to_char(audit_date,'yyyy-mm-dd hh24:mi:ss') audit_date,audit_remark,audit_type_code,remind_code_a,to_char(remind_date_a,'yyyy-mm-dd hh24:mi:ss') remind_date_a,remind_code_b,to_char(remind_date_b,'yyyy-mm-dd hh24:mi:ss') remind_date_b,apply_no,to_char(apply_batch_id) apply_batch_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3 
  FROM tf_b_resapply_detail
 WHERE apply_detail_no = :APPLY_DETAIL_NO