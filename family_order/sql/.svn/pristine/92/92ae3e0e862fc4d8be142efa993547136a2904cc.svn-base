SELECT produce_id,to_char(batch_id) batch_id,res_type_code,eparchy_code,city_code,depart_id,design_code,brand_code,audit_type_code,audit_state_code,to_char(plan_start_time,'yyyy-mm-dd hh24:mi:ss') plan_start_time,to_char(plan_end_time,'yyyy-mm-dd hh24:mi:ss') plan_end_time,to_char(finish_time,'yyyy-mm-dd hh24:mi:ss') finish_time,assign_staff_id,to_char(assign_num) assign_num,factory_code,apply_status,produce_contract_id,to_char(produce_date,'yyyy-mm-dd hh24:mi:ss') produce_date,priority,cancel_reason,to_char(sale_price) sale_price,to_char(sale_money) sale_money,sale_type_code,pay_type_code,fee_staff_id,to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,apply_no,to_char(apply_batch_id) apply_batch_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag 
  FROM tf_b_res_produce_main
where  plan_start_time >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
and plan_start_time <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
and res_type_code=:RES_TYPE_CODE
and (:APPLY_AREA_CODE is null or eparchy_code = :APPLY_AREA_CODE)
and (:APPLY_STATUS is null or apply_status =:APPLY_STATUS )
and (:AUDIT_STATE_CODE is null or audit_state_code = :AUDIT_STATE_CODE)
and (:FACTORY_CODE is null or factory_code = :FACTORY_CODE)
order by plan_start_time desc,apply_status desc,priority desc