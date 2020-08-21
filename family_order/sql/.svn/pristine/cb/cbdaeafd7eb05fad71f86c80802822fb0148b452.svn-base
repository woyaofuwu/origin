SELECT to_char(log_id) log_id,res_type_code,start_res_no,end_res_no,res_kind_code,capacity_type_code,to_char(time_in,'yyyy-mm-dd hh24:mi:ss') time_in,staff_id_in,declare_staff_id,to_char(declare_date,'yyyy-mm-dd hh24:mi:ss') declare_date,declare_area_code,deal_state_code,factory_code,chip_factory_code,to_char(oper_num) oper_num,bad_type_code,declare_info,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag
  FROM tf_b_res_quality
 WHERE (:DECLARE_AREA_CODE is null or declare_area_code=:DECLARE_AREA_CODE)
   AND (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND time_in>=TO_DATE(:TIME_IN_S, 'YYYY-MM-DD HH24:MI:SS')
   AND time_in<=TO_DATE(:TIME_IN_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:DEAL_STATE_CODE is null or deal_state_code=:DEAL_STATE_CODE)