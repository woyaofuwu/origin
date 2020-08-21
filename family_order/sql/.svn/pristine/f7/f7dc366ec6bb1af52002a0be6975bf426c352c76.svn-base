SELECT eparchy_code,design_code,res_type_code,res_kind_code,capacity_type_code,to_char(sum(device_num)) device_num 
  FROM tf_b_design_use
 WHERE update_time>=TO_DATE(:UPDATE_TIME_S, 'YYYY-MM-DD HH24:MI:SS') 
   AND update_time<=TO_DATE(:UPDATE_TIME_E, 'YYYY-MM-DD HH24:MI:SS') 
   AND (:DESIGN_CODE is null or design_code=:DESIGN_CODE)
   AND (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
   GROUP BY eparchy_code,design_code,res_type_code,res_kind_code,capacity_type_code