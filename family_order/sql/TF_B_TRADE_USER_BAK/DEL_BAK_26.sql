delete from tf_b_trade_product_bak
where trade_id=to_number(:TRADE_ID)
  and accept_month=to_number(substr(:TRADE_ID,5,2))