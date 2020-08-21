--IS_CACHE=Y
SELECT product_id,package_id_a,package_id_b,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM td_b_prod_package_limit
 WHERE limit_tag=:LIMIT_TAG
   AND end_date >= sysdate
   AND start_date <= sysdate
   AND package_id_b > -1
   AND product_id=:PRODUCT_ID
   AND package_id_a=:PACKAGE_ID_A
   and  (eparchy_code = :EPARCHY_CODE or eparchy_code = 'ZZZZ')