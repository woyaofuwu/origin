INSERT INTO tf_bh_trade 
SELECT *
  FROM tf_b_trade
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND cancel_tag = :CANCEL_TAG