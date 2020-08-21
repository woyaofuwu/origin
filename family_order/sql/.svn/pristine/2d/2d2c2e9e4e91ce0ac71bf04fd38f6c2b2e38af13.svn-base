--IS_CACHE=Y
SELECT service_id,item_index,item_field_code,item_field_name,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_b_serv_itemb a
 WHERE a.service_id = :SERVICE_ID
   AND a.item_field_code = :ITEM_FIELD_CODE
   AND a.rsrv_str1 = 'DCNT'
   AND (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   AND SYSDATE BETWEEN a.start_date AND a.end_date