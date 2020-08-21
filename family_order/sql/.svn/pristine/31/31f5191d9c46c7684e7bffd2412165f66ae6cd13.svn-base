SELECT biz_code,limit_obj_b,obj_name,obj_type,limit_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,brand_code,product_id 
  FROM td_b_operation_limit a
 WHERE a.biz_code = :BIZ_CODE
   AND a.limit_type_code = '0'
   AND a.obj_type = 0
   AND (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   AND (a.brand_code = :BRAND_CODE OR a.brand_code = 'ZZZZ')
   AND (a.product_id = :PRODUCT_ID OR a.product_id = -1)
   AND SYSDATE BETWEEN a.start_date AND a.end_date
   AND EXISTS (SELECT 1 FROM tf_f_user_svc
                WHERE partition_id = MOD(to_number(:USER_ID),10000)
                  AND user_id = to_number(:USER_ID)
                  AND service_id = to_number(a.limit_obj_b)
                  AND SYSDATE BETWEEN start_date AND end_date) 
UNION ALL
SELECT biz_code,limit_obj_b,obj_name,obj_type,limit_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,brand_code,product_id 
  FROM td_b_operation_limit b
 WHERE b.biz_code = :BIZ_CODE
   AND b.limit_type_code = '0'
   AND b.obj_type = 1
   AND (b.eparchy_code = :EPARCHY_CODE OR b.eparchy_code = 'ZZZZ')
   AND (b.brand_code = :BRAND_CODE OR b.brand_code = 'ZZZZ')
   AND (b.product_id = :PRODUCT_ID OR b.product_id = -1)
   AND SYSDATE BETWEEN b.start_date AND b.end_date
   AND EXISTS (SELECT 1 FROM tf_f_user_discnt
                WHERE partition_id = MOD(to_number(:USER_ID),10000)
                  AND user_id = to_number(:USER_ID)
                  AND discnt_code = to_number(b.limit_obj_b)
                  AND SYSDATE BETWEEN start_date AND end_date) 
UNION ALL
SELECT biz_code,limit_obj_b,obj_name,obj_type,limit_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,brand_code,product_id 
  FROM td_b_operation_limit c
 WHERE c.biz_code = :BIZ_CODE
   AND c.limit_type_code = '0'
   AND c.obj_type = 2
   AND (c.eparchy_code = :EPARCHY_CODE OR c.eparchy_code = 'ZZZZ')
   AND (c.brand_code = :BRAND_CODE OR c.brand_code = 'ZZZZ')
   AND (c.product_id = :PRODUCT_ID OR c.product_id = -1)
   AND SYSDATE BETWEEN c.start_date AND c.end_date
   AND EXISTS (SELECT 1 FROM tf_f_user_plat_order
                WHERE partition_id = MOD(to_number(:USER_ID),10000)
                  AND user_id = to_number(:USER_ID)
                  AND biz_code = c.limit_obj_b
                  AND SYSDATE BETWEEN start_date AND end_date)