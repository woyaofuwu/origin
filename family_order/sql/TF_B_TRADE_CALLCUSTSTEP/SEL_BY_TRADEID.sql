SELECT to_char(trade_id) trade_id,callcust_item_name,callcust_item_type,callcust_item_code,step,next_id 
  FROM tf_b_trade_callcuststep
 WHERE trade_id=TO_NUMBER(:TRADE_ID)