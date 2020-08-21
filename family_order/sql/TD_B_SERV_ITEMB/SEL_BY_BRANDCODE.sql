--IS_CACHE=Y
SELECT service_id,item_index,item_field_code,item_field_name,rsrv_str1,
        rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5
  FROM td_b_serv_itemb a
 WHERE (a.eparchy_code=:EPARCHY_CODE OR a.eparchy_code='ZZZZ')
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND not exists (select 1 from td_s_commpara
                   where param_attr=17
                     and para_code1=to_char(a.service_id))
UNION ALL
SELECT service_id,item_index,item_field_code,item_field_name,rsrv_str1,
        rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5
  FROM td_b_serv_itemb a,td_s_commpara b
 WHERE (a.eparchy_code=:EPARCHY_CODE OR a.eparchy_code='ZZZZ')
   AND (b.eparchy_code=:EPARCHY_CODE OR b.eparchy_code='ZZZZ')
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND sysdate BETWEEN b.start_date AND b.end_date
   AND b.subsys_code='CSM'
   AND b.param_attr=17
   AND b.para_code1=to_char(a.service_id)
   AND (b.para_code2=:BRAND_CODE OR b.para_code2='ZZZZ')
   AND b.para_code3=a.item_field_code
 ORDER BY 1,2,3