UPDATE td_b_product_release
   SET product_id=:PRODUCT_ID,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),release_eparchy_code=:RELEASE_EPARCHY_CODE,release_staff_id=:RELEASE_STAFF_ID,release_depart_id=:RELEASE_DEPART_ID,release_time=sysdate  
 WHERE product_id = :PRODUCT_ID
AND start_date = TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
AND (release_eparchy_code = :RELEASE_EPARCHY_CODE)