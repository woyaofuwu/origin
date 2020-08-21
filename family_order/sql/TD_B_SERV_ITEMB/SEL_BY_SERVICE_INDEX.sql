--IS_CACHE=Y
SELECT service_id,item_index,item_field_code,item_field_name,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_serv_itemb
 WHERE (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
   AND sysdate BETWEEN start_date AND end_date
   AND service_id=:SERVICE_ID
   AND item_index=:ITEM_INDEX
 ORDER BY 1,2,3