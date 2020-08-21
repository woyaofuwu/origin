--IS_CACHE=Y
SELECT trade_type_code paracode,trade_type paraname
 FROM td_s_tradetype
 WHERE trade_type_code < 5000
   AND tag_set like '1%'
   AND eparchy_code=:TRADE_EPARCHY_CODE
   order by trade_type_code