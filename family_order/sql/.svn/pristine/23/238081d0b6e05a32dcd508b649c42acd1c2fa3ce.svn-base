--IS_CACHE=Y
SELECT eparchy_code,res_type_code,depart_id,depart_code,depart_kind_code,depart_name,depart_frame,valid_flag,area_code,parent_depart_id,manager_id,order_no,to_char(warnning_value_u) warnning_value_u,to_char(warnning_value_d) warnning_value_d,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,depart_level,stock_level,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag 
  FROM td_s_assignrule
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND (:AREA_CODE IS NULL OR (:AREA_CODE IS NOT NULL AND area_code=:AREA_CODE))
   AND (  depart_frame LIKE :DEPART_FRAME||'%' OR 
         (depart_frame LIKE :PREDEPART_FRAME||'%' AND parent_depart_id=:PARENT_DEPART_ID) OR
          depart_kind_code='00A'
        ) 
   AND valid_flag='0'
   --AND depart_kind_code NOT IN ('3','4','5','6','7','8','B')
   AND depart_kind_code NOT IN (SELECT depart_kind_code 
                                  FROM td_m_departkind
                                 WHERE code_type_code='0'
                                   AND eparchy_code=:EPARCHY_CODE)
   ORDER BY depart_level,depart_code