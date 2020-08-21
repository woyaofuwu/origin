--IS_CACHE=Y
SELECT service_id,item_index,item_field_code,item_field_name,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM td_b_serv_itemb
 WHERE (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
   AND sysdate BETWEEN start_date AND end_date
 ORDER BY 1,2,3