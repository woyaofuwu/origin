--IS_CACHE=Y
SELECT para_code,para_name,para_type_code,decode(rsrv_tag1,'0','动态','1','静态',rsrv_tag1) rsrv_tag1,ip_address,eparchy_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM td_b_apnservice
 WHERE para_type_code=:PARA_TYPE_CODE
   AND (eparchy_code=:EPARCHY_CODE or eparchy_code = 'ZZZZ')
   AND sysdate between start_date and end_date