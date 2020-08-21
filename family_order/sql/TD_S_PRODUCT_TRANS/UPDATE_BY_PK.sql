UPDATE td_s_product_trans
   SET right_code=:RIGHT_CODE,trans_fee=TO_NUMBER(:TRANS_FEE),fee_mode=:FEE_MODE,fee_type_code=:FEE_TYPE_CODE,enable_tag=:ENABLE_TAG,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE
 WHERE product_id_a=:PRODUCT_ID_A
   AND product_id_b=:PRODUCT_ID_B
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')