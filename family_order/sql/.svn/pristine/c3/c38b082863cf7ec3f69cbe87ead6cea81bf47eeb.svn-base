UPDATE td_s_product_trans
   SET start_date=SYSDATE,end_date=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE
 WHERE product_id_a=:PRODUCT_ID_A
   AND product_id_b=:PRODUCT_ID_B
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')