--IS_CACHE=Y
SELECT pay_mode_code paracode,pay_mode paraname FROM td_s_paymode
 WHERE sysdate BETWEEN start_date AND end_date
   AND eparchy_code = :TRADE_EPARCHY_CODE