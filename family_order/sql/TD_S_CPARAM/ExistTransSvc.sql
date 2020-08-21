SELECT COUNT(*) recordcount
 FROM tf_b_trade_svc a
 WHERE a.trade_id = :TRADE_ID
  AND a.service_id NOT IN (8,124,125,127,126)
  AND EXISTS(
  SELECT 1 FROM tf_b_trade_svc b
   WHERE a.trade_id = b.trade_id
     AND b.service_id IN(8,124,125,127,126)
  )