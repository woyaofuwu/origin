SELECT eparchy_code,contract_id,res_type_code,res_kind_code,capacity_type_code,design_code,version,to_char(oper_num) oper_num,to_char(device_price) device_price,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,factory_code,area_code,contract_index,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,contract_exec_declare,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag 
  FROM tf_b_res_contract_exec
 WHERE (:CONTRACT_ID is null or contract_id=:CONTRACT_ID)
   AND (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND (:FACTORY_CODE is null or factory_code=:FACTORY_CODE)