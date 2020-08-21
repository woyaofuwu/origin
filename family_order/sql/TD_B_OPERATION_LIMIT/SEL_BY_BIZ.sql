--IS_CACHE=Y
SELECT biz_code,limit_obj_b,obj_name,obj_type,limit_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,brand_code,product_id 
  FROM td_b_operation_limit
 WHERE biz_code=:BIZ_CODE
   AND sysdate BETWEEN start_date AND end_date
   AND eparchy_code=:EPARCHY_CODE