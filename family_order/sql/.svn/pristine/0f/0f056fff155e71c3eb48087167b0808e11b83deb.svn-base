UPDATE tf_b_cardsale_log
   SET sub_log_id=TO_NUMBER(:SUB_LOG_ID),sale_type_code=:SALE_TYPE_CODE_D,remark=:REMARK,rsrv_date1=sysdate,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3  
 WHERE log_id=TO_NUMBER(:LOG_ID)
   AND (:EPARCHY_CODE is null OR eparchy_code=:EPARCHY_CODE)
   AND (:CITY_CODE is null OR city_code=:CITY_CODE)
   AND (:SALE_TYPE_CODE_C is null OR sale_type_code=:SALE_TYPE_CODE_C)
   AND (:STOCK_ID is null OR stock_id=:STOCK_ID)