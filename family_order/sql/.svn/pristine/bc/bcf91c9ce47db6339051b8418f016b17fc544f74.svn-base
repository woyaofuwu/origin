--IS_CACHE=Y
SELECT eparchy_code,right_code_a,right_code_b,data_type,DECODE(right_attr,'1','单个数据权限','2','角色权限','单个功能权限') right_attr,DECODE(check_flag,'1','依赖','互斥') check_flag,DECODE(right_tag,'1','有效','无效') right_tag,right_frame,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_right_relation
 WHERE eparchy_code=:EPARCHY_CODE
   AND (:CHECK_FLAG IS NULL OR check_flag=:CHECK_FLAG)
   AND (:RIGHT_TAG IS NULL OR right_tag=:RIGHT_TAG)
   AND start_date<=SYSDATE
   AND end_date>=SYSDATE
   AND (:RSRV_TAG1 IS NULL OR rsrv_tag1=:RSRV_TAG1)
 ORDER BY check_flag,right_attr,right_code_a