SELECT right_code,opered_staff_id,right_attr,to_char(log_id) log_id,oper_type,right_type,role_code,area_code,oper_staff_id,oper_depart_id,to_char(oper_date,'yyyy-mm-dd hh24:mi:ss') oper_date,right_class,oper_special,extend_value1,extend_value2,rsvalue1,rsvalue2,use_tag,times,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark 
  FROM tl_m_rightlog
 WHERE right_code=:RIGHT_CODE
   AND opered_staff_id=:OPERED_STAFF_ID
   AND right_attr=:RIGHT_ATTR
   AND (:OPER_TYPE IS NULL OR oper_type=:OPER_TYPE) 
   AND (:RIGHT_TYPE IS NULL OR right_type=:RIGHT_TYPE) 
   AND (:USE_TAG IS NULL OR use_tag=:USE_TAG)