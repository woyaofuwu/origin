UPDATE td_b_product_svcprice
   SET b_price_code=:B_PRICE_CODE,a_price_code=:A_PRICE_CODE,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate
 WHERE product_id=:PRODUCT_ID
   AND service_id=:SERVICE_ID
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')