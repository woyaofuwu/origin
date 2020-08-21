--IS_CACHE=Y
SELECT eparchy_code,res_type_code,depart_id,depart_code,depart_kind_code,depart_name,depart_frame,valid_flag,area_code,parent_depart_id,manager_id,order_no,to_char(nvl(warnning_value_u,0)) warnning_value_u,to_char(nvl(warnning_value_d,0)) warnning_value_d,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,depart_level,stock_level,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag 
FROM td_s_assignrule
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND valid_flag='0'
   AND (:AREA_CODE IS NULL OR area_code=:AREA_CODE)
   AND (:PARENT_DEPART_ID IS NULL OR (parent_depart_id=:PARENT_DEPART_ID OR depart_kind_code='00A'))
ORDER BY depart_level,order_no