--IS_CACHE=Y
SELECT para_type_code,para_type,parent_type_code,eparchy_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM td_b_apncatalog
 start with parent_type_code=:PARENT_TYPE_CODE AND (eparchy_code=:EPARCHY_CODE or eparchy_code = 'ZZZZ') AND sysdate between start_date and end_date
 connect by PRIOR para_type_code =  parent_type_code