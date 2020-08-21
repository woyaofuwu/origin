--IS_CACHE=Y
SELECT trade_type_code,brand_code,product_id,limit_code,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,right_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_s_trade_speclimit A
 WHERE (trade_type_code=:TRADE_TYPE_CODE OR trade_type_code='-1')
   AND (brand_code=:BRAND_CODE OR brand_code = 'ZZZZ')
   AND (product_id=:PRODUCT_ID OR product_id = '-1')
   AND SYSDATE BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code = 'ZZZZ')
    AND rsrv_str5 Like :RSRV_STR5
   AND (right_code IS NULL 
     OR (NOT EXISTS (SELECT d.data_code FROM tf_m_staffdataright C,tf_m_roledataright D
                          WHERE C.staff_id = :STAFF_ID
                            AND C.data_code = D.role_code
                            AND C.right_attr = '1'   --权限属性：1-数据角色权限
                            AND C.right_tag = '1'    --权限标志：1-有效
                            AND D.data_type = '1'    --数据类型：1-数据特权
                            AND D.data_code = A.right_code))    
     AND NOT EXISTS (SELECT e.data_code FROM tf_m_staffdataright e
                                WHERE e.staff_id = :STAFF_ID
                                  AND e.right_attr = '0'
                                  AND e.data_type = '1'
                                  AND e.right_tag = '1'
                                  AND e.data_code = A.right_code))