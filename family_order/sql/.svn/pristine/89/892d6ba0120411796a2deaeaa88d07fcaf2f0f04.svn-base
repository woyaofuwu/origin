SELECT to_char(log_id) log_id,res_no,res_type_code,res_state_code,res_kind_code,capacity_type_code,eparchy_code,city_code,stock_id,stock_level,to_char(oper_date,'yyyy-mm-dd hh24:mi:ss') oper_date,oper_staff_id,oper_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7 
  FROM tf_b_res_middle
 WHERE res_no>=:RES_NO_S
   AND res_no<=:RES_NO_E
   AND (:LOG_ID is null or log_id=TO_NUMBER(:LOG_ID))
   AND (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:RES_STATE_CODE is null or res_state_code=:RES_STATE_CODE)
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
   AND rsrv_str1=:RSRV_STR1