SELECT to_char(log_id) log_id,oper_area_code,res_type_code,res_trade_type_code,start_value1,end_value1,start_value2,end_value2,start_value3,end_value3,to_char(oper_num) oper_num,oper_info,to_char(batch_id) batch_id,remark,rsvalue1,rsvalue2,to_char(oper_date,'yyyy-mm-dd hh24:mi:ss') oper_date,oper_staff_id,oper_depart_id,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7 
  FROM tf_b_cen_manager_log
 WHERE (:OPER_AREA_CODE IS NULL OR oper_area_code=:OPER_AREA_CODE) 
   AND (:RES_TYPE_CODE IS NULL OR res_type_code=:RES_TYPE_CODE) 
   AND (:RES_TRADE_TYPE_CODE IS NULL OR res_trade_type_code=:RES_TRADE_TYPE_CODE) 
   AND (:START_VALUE1 IS NULL OR start_value1=:START_VALUE1) 
   AND (:END_VALUE1 IS NULL OR end_value1=:END_VALUE1) 
   AND oper_date>=TO_DATE(:OPER_DATE_S, 'YYYY-MM-DD HH24:MI:SS')
   AND oper_date<=TO_DATE(:OPER_DATE_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:OPER_STAFF_ID IS NULL OR oper_staff_id=:OPER_STAFF_ID)