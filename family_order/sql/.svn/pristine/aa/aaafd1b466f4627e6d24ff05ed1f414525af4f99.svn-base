UPDATE td_s_productlimit
   SET product_id_a=:PRODUCT_ID_A,product_id_b=:PRODUCT_ID_B,limit_tag=:LIMIT_TAG,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS')  
WHERE product_id_a =:PRODUCT_ID_A 
AND  product_id_b =:PRODUCT_ID_B 
AND START_DATE = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')