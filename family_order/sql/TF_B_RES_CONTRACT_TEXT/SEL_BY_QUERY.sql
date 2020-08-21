SELECT eparchy_code,contract_name,company_a,company_b,code_type_code,code_state_code,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,contract_content,contract_index,contract_product,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag
 FROM TF_B_RES_CONTRACT_TEXT
 WHERE (:CODE_TYPE_CODE is null or code_type_code = :CODE_TYPE_CODE)
 AND (:CODE_STATE_CODE is null or code_state_code = :CODE_STATE_CODE)
 AND time_in >=to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
 AND time_in <=to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 AND (:CONTRACT_INDEX is null or contract_index = :CONTRACT_INDEX)