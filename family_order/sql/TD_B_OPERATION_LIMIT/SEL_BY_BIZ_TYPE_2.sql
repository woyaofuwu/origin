SELECT biz_code,limit_obj_b,obj_name,obj_type,limit_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,brand_code,product_id 
  FROM td_b_operation_limit d
 WHERE d.biz_code = :BIZ_CODE
   AND d.limit_type_code = '2'
   AND d.obj_type = 0
   AND (d.eparchy_code = :EPARCHY_CODE OR d.eparchy_code = 'ZZZZ')
   AND (d.brand_code = :BRAND_CODE OR d.brand_code = 'ZZZZ')
   AND (d.product_id = :PRODUCT_ID OR d.product_id = -1)
   AND SYSDATE BETWEEN d.start_date AND d.end_date
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_svc
                WHERE partition_id = MOD(to_number(:USER_ID),10000)
                  AND user_id = to_number(:USER_ID)
                  AND service_id = to_number(d.limit_obj_b)
                  AND SYSDATE BETWEEN start_date AND end_date) 
UNION ALL
SELECT biz_code,limit_obj_b,obj_name,obj_type,limit_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,brand_code,product_id 
  FROM td_b_operation_limit e
 WHERE e.biz_code = :BIZ_CODE
   AND e.limit_type_code = '2'
   AND e.obj_type = 1
   AND (e.eparchy_code = :EPARCHY_CODE OR e.eparchy_code = 'ZZZZ')
   AND (e.brand_code = :BRAND_CODE OR e.brand_code = 'ZZZZ')
   AND (e.product_id = :PRODUCT_ID OR e.product_id = -1)
   AND SYSDATE BETWEEN e.start_date AND e.end_date
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt
                WHERE partition_id = MOD(to_number(:USER_ID),10000)
                  AND user_id = to_number(:USER_ID)
                  AND discnt_code = to_number(e.limit_obj_b)
                  AND SYSDATE BETWEEN start_date AND end_date) 
UNION ALL
SELECT biz_code,limit_obj_b,obj_name,obj_type,limit_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,brand_code,product_id 
  FROM td_b_operation_limit f
 WHERE f.biz_code = :BIZ_CODE
   AND f.limit_type_code = '2'
   AND f.obj_type = 2
   AND (f.eparchy_code = :EPARCHY_CODE OR f.eparchy_code = 'ZZZZ')
   AND (f.brand_code = :BRAND_CODE OR f.brand_code = 'ZZZZ')
   AND (f.product_id = :PRODUCT_ID OR f.product_id = -1)
   AND SYSDATE BETWEEN f.start_date AND f.end_date
   AND EXISTS (SELECT 1 FROM tf_f_user_plat_order
                WHERE partition_id = MOD(to_number(:USER_ID),10000)
                  AND user_id = to_number(:USER_ID)
                  AND biz_code = f.limit_obj_b
                  AND SYSDATE BETWEEN start_date AND end_date)