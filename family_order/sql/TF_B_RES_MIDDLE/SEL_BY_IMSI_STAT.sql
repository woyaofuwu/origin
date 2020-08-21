SELECT eparchy_code,city_code,stock_id,res_type_code,res_kind_code,count(rsrv_str1) rsrv_str3  
  FROM tf_b_res_middle
 WHERE (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
   AND (:CITY_CODE is null or city_code=:CITY_CODE)
   AND (:STOCK_ID is null or stock_id=:STOCK_ID)
   AND rsrv_str1=:RSRV_STR1
 GROUP BY eparchy_code,city_code,stock_id,res_type_code,res_kind_code