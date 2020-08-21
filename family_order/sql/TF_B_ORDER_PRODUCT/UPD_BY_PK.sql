UPDATE tf_b_order_product
   SET accept_month=:ACCEPT_MONTH,order_id=TO_NUMBER(:ORDER_ID),cancel_tag=:CANCEL_TAG,trade_type_code=:TRADE_TYPE_CODE,prod_order_id=TO_NUMBER(:PROD_ORDER_ID),brand_code=:BRAND_CODE,product_id=:PRODUCT_ID,product_mode=:PRODUCT_MODE,comp_tag=:COMP_TAG,oper_fee=TO_NUMBER(:OPER_FEE),foregift=TO_NUMBER(:FOREGIFT),advance_pay=TO_NUMBER(:ADVANCE_PAY),fee_state=:FEE_STATE,fee_staff_id=:FEE_STAFF_ID,fee_time=TO_DATE(:FEE_TIME, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE accept_month=:ACCEPT_MONTH
   AND order_id=TO_NUMBER(:ORDER_ID)
   AND cancel_tag=:CANCEL_TAG