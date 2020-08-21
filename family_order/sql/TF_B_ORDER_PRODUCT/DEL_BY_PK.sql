DELETE FROM tf_b_order_product
 WHERE accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND order_id=TO_NUMBER(:ORDER_ID)