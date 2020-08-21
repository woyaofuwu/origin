--IS_CACHE=Y
SELECT service_id,item_index,item_type_code,item_lable,item_hint,item_init_value,item_can_null,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM td_b_serv_itema
 WHERE (eparchy_code=:EPARCHY_CODE OR eparchy_code = 'ZZZZ')
   AND sysdate BETWEEN start_date AND end_date
 ORDER BY 1,2,3