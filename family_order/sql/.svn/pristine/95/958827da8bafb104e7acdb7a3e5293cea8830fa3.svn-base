--IS_CACHE=Y
SELECT product_id,service_id,b_price_code,a_price_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_product_svcprice
 WHERE product_id=:PRODUCT_ID
   AND service_id=:SERVICE_ID
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')