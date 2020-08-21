--IS_CACHE=Y
SELECT BATCH_OPER_TYPE paracode,BATCH_OPER_NAME paraname FROM td_b_opertype
 WHERE sysdate BETWEEN start_date AND end_date AND eparchy_code = :TRADE_EPARCHY_CODE