SELECT  COUNT(serial_number) serial_number,eparchy_code,city_code_o,stock_id_o,rsrv_str1 
  FROM tf_b_reuse_log
 WHERE eparchy_code=:EPARCHY_CODE
   AND (:CITY_CODE_O is null or city_code_o=:CITY_CODE_O)
   AND (:STOCK_ID_O is null or stock_id_o=:STOCK_ID_O)
   AND back_time>=TO_DATE(:BACK_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND back_time<=TO_DATE(:BACK_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:RSRV_STR1 IS NULL OR rsrv_str1=:RSRV_STR1)
 GROUP BY eparchy_code,city_code_o,stock_id_o,rsrv_str1