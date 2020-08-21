update TF_B_TRADE_GRP_MERCH  m set  m.merch_order_id=:MERCH_ORDER_ID
 WHERE m.trade_id = TO_NUMBER(:TRADE_ID)
   AND m.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))