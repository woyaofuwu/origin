--IS_CACHE=Y
SELECT PSPT_TYPE_CODE paracode,PSPT_TYPE paraname FROM td_s_passporttype
 WHERE sysdate BETWEEN start_date AND end_date
   AND eparchy_code = :TRADE_EPARCHY_CODE
   AND (cust_type IS NULL OR cust_type = '1')