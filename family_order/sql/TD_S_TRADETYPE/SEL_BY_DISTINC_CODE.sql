--IS_CACHE=Y
SELECT DISTINCT trade_type_code,trade_type 
  FROM td_s_tradetype
 WHERE trade_type_code=:TRADE_TYPE_CODE
   AND SYSDATE BETWEEN start_date AND end_date