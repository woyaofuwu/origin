--IS_CACHE=Y
SELECT FEEITEM_CODE paracode,FEEITEM_NAME paraname FROM td_b_feeitem
 WHERE sysdate BETWEEN start_date AND end_date
  AND (eparchy_code = :TRADE_EPARCHY_CODE OR eparchy_code='ZZZZ')