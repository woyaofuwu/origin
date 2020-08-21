SELECT COUNT(1) recordcount
  FROM tf_bh_trade
 WHERE accept_date > trunc(SYSDATE) - to_number(:MONTH)
   AND trade_type_code = to_number(:TRADE_TYPE_CODE)