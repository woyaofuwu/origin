--IS_CACHE=Y
SELECT USER_TYPE_CODE paracode,USER_TYPE paraname FROM td_b_usertype
 WHERE sysdate BETWEEN start_date AND end_date
   AND USER_ATTR = '2'
   AND eparchy_code = :TRADE_EPARCHY_CODE