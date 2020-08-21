--IS_CACHE=Y
SELECT IN_MODE_CODE paracode,IN_MODE paraname FROM td_s_inmode
 WHERE sysdate BETWEEN start_date AND end_date
   AND eparchy_code = :TRADE_EPARCHY_CODE