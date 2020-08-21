SELECT eparchy_code,city_code,res_type_code,res_kind_code,to_char(sum(sale_num)) sale_num,value_code,stock_id 
  FROM tf_b_cardsale_log
 WHERE (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:RES_KIND_CODE is null or res_kind_code=:RES_KIND_CODE)
   AND (:VALUE_CODE is null or value_code=:VALUE_CODE)
   AND (:CITY_CODE is null or city_code=:CITY_CODE)
   AND sale_time>=TO_DATE(:SALE_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND sale_time<=TO_DATE(:SALE_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:STOCK_ID is null or stock_id=:STOCK_ID)
   AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
 GROUP BY eparchy_code,city_code,res_type_code,res_kind_code,value_code,stock_id