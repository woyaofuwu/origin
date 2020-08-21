update TF_B_TRADE_GRP_MERCHP  m set  m.product_order_id=:PRODUCT_ORDER_ID
 WHERE m.trade_id = TO_NUMBER(:TRADE_ID)
   AND m.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   and m.product_offer_id=:PRODUCT_OFFER_ID