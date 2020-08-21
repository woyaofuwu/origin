SELECT /*+ first_rows(1)*/
   COUNT(1) recordcount
 from tf_b_trade
 where rsrv_str6 = :USER_ID
  and trade_type_code = 245
  and trade_id <> :TRADE_ID
  and rownum < 2