select trade_id from (
SELECT max(trade_id) trade_id
  FROM tf_bh_trade
 WHERE trade_type_code=to_number(:TRADE_TYPE_CODE)
   AND serial_number=:SERIAL_NUMBER
   AND cancel_tag=:CANCEL_TAG
) where trade_id is not null