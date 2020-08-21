--IS_CACHE=Y
SELECT eparchy_code,right_code_a,right_code_b,data_type,right_attr,check_flag,right_tag,right_frame,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_right_relation
 WHERE eparchy_code=:EPARCHY_CODE
   AND right_code_a=:RIGHT_CODE_A
   AND right_code_b=:RIGHT_CODE_B
   AND (:DATA_TYPE IS NULL OR data_type=:DATA_TYPE)