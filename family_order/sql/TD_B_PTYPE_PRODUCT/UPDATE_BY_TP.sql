UPDATE td_b_ptype_product
   SET product_type_code=:PRODUCT_TYPE_CODE,product_id=:PRODUCT_ID,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS')  
WHERE product_id = :PRODUCT_ID
AND start_date = TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
AND (product_type_code= :PRODUCT_TYPE_CODE)