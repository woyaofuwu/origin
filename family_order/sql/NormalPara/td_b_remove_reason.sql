--IS_CACHE=Y
SELECT remove_reason_code paracode, remove_reason paraname FROM td_b_remove_reason
  WHERE :TRADE_EPARCHY_CODE IS NOT NULL OR :TRADE_EPARCHY_CODE IS NULL ORDER BY 1