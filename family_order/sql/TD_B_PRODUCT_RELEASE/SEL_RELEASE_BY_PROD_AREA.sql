--IS_CACHE=Y
SELECT product_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,release_eparchy_code,release_staff_id,release_depart_id,to_char(release_time,'yyyy-mm-dd hh24:mi:ss') release_time 
  FROM td_b_product_release
 WHERE product_id = :PRODUCT_ID
   AND (release_eparchy_code = :RELEASE_EPARCHY_CODE OR release_eparchy_code = 'ZZZZ')
   AND SYSDATE BETWEEN start_date AND end_date